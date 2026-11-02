package com.guiachevrolet.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.guiachevrolet.data.local.entity.VehicleEntity
import com.guiachevrolet.data.repository.ChevroletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

sealed interface PdfLoadState {
    data object Loading : PdfLoadState
    data class Downloading(val progressPercent: Int) : PdfLoadState
    data class Ready(val pageCount: Int, val isCachedOffline: Boolean) : PdfLoadState
    data class Error(val message: String) : PdfLoadState
}

data class ManualViewerUiState(
    val vehicle: VehicleEntity? = null,
    val manualTitle: String = "Manual do Proprietário",
    val loadState: PdfLoadState = PdfLoadState.Loading
)

class ManualViewerViewModel(
    private val repository: ChevroletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualViewerUiState())
    val uiState: StateFlow<ManualViewerUiState> = _uiState.asStateFlow()

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private val renderMutex = Mutex()
    private val pageCache = object : LinkedHashMap<Int, Bitmap>(12, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, Bitmap>?): Boolean {
            return size > 10
        }
    }

    fun loadManualForVehicle(context: Context, vehicleId: Long, forceRedownload: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadState = PdfLoadState.Loading) }
            val vehicle = repository.getVehicleById(vehicleId)
            if (vehicle == null) {
                _uiState.update {
                    it.copy(loadState = PdfLoadState.Error("Veículo não encontrado."))
                }
                return@launch
            }

            val manualEntity = repository.getManualForModel(vehicle.model)
            val pdfUrl = vehicle.manualUrl.ifBlank { manualEntity.pdfUrl }

            _uiState.update {
                it.copy(
                    vehicle = vehicle,
                    manualTitle = manualEntity.title
                )
            }

            val sanitizedModel = vehicle.model.lowercase().replace(Regex("[^a-z0-9]"), "_")
            val manualsDir = File(context.filesDir, "manuals").apply { mkdirs() }
            val localPdfFile = File(manualsDir, "manual_${sanitizedModel}.pdf")

            if (forceRedownload && localPdfFile.exists()) {
                localPdfFile.delete()
            }

            val alreadyCached = localPdfFile.exists() && localPdfFile.length() > 10_000L

            if (!alreadyCached) {
                val downloadResult = downloadPdfToFile(pdfUrl, localPdfFile) { progress ->
                    _uiState.update {
                        it.copy(loadState = PdfLoadState.Downloading(progress))
                    }
                }
                if (downloadResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            loadState = PdfLoadState.Error(
                                "Não foi possível baixar o manual oficial do Chevrolet ${vehicle.model}. Verifique sua conexão com a internet no primeiro acesso."
                            )
                        )
                    }
                    return@launch
                }
            }

            openPdfRenderer(localPdfFile, isCachedOffline = true)
        }
    }

    private suspend fun downloadPdfToFile(
        urlString: String,
        destinationFile: File,
        onProgress: (Int) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val tempFile = File(destinationFile.parentFile, "${destinationFile.name}.tmp")
        runCatching {
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 20_000
                readTimeout = 30_000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android; GuiaChevroletApp)")
            }
            connection.connect()

            if (connection.responseCode !in 200..299) {
                throw IllegalStateException("HTTP ${connection.responseCode}")
            }

            val totalBytes = connection.contentLengthLong
            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(16 * 1024)
                    var downloadedBytes = 0L
                    var bytesRead: Int
                    var lastReportedProgress = -1

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            val progress = ((downloadedBytes * 100) / totalBytes).toInt().coerceIn(0, 100)
                            if (progress != lastReportedProgress) {
                                lastReportedProgress = progress
                                onProgress(progress)
                            }
                        }
                    }
                    output.flush()
                }
            }

            if (tempFile.length() < 10_000L) {
                tempFile.delete()
                throw IllegalStateException("Arquivo PDF inválido ou incompleto.")
            }

            if (destinationFile.exists()) {
                destinationFile.delete()
            }
            tempFile.renameTo(destinationFile)
            Unit
        }.onFailure {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }

    private suspend fun openPdfRenderer(file: File, isCachedOffline: Boolean) = withContext(Dispatchers.IO) {
        runCatching {
            renderMutex.withLock {
                closeRendererInternal()
                val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(descriptor)
                fileDescriptor = descriptor
                pdfRenderer = renderer
                renderer.pageCount
            }
        }.onSuccess { count ->
            _uiState.update {
                it.copy(loadState = PdfLoadState.Ready(pageCount = count, isCachedOffline = isCachedOffline))
            }
        }.onFailure {
            file.delete()
            _uiState.update {
                it.copy(
                    loadState = PdfLoadState.Error(
                        "Erro ao abrir o arquivo PDF do manual. Tente baixar novamente."
                    )
                )
            }
        }
    }

    suspend fun renderPage(pageIndex: Int, targetWidthPx: Int): Bitmap? = withContext(Dispatchers.IO) {
        renderMutex.withLock {
            pageCache[pageIndex]?.let { cached ->
                if (!cached.isRecycled) return@withLock cached
            }

            val renderer = pdfRenderer ?: return@withLock null
            if (pageIndex !in 0 until renderer.pageCount) return@withLock null

            renderer.openPage(pageIndex).use { page ->
                val width = targetWidthPx.coerceAtLeast(720)
                val height = ((width.toFloat() / page.width.coerceAtLeast(1)) * page.height).toInt().coerceAtLeast(1)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                pageCache[pageIndex] = bitmap
                bitmap
            }
        }
    }

    private fun closeRendererInternal() {
        pageCache.clear()
        runCatching { pdfRenderer?.close() }
        runCatching { fileDescriptor?.close() }
        pdfRenderer = null
        fileDescriptor = null
    }

    override fun onCleared() {
        super.onCleared()
        closeRendererInternal()
    }

    companion object {
        fun provideFactory(repository: ChevroletRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ManualViewerViewModel(repository) as T
                }
            }
    }
}
