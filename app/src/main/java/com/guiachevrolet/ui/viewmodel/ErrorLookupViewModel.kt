package com.guiachevrolet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.guiachevrolet.data.local.entity.ErrorCodeEntity
import com.guiachevrolet.data.local.entity.VehicleEntity
import com.guiachevrolet.data.repository.ChevroletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LookupResultState {
    data object Idle : LookupResultState
    data object Loading : LookupResultState
    data class Found(val errorCode: ErrorCodeEntity) : LookupResultState
    data class NotFound(val searchedCode: Int, val vehicleModel: String) : LookupResultState
    data class InvalidInput(val message: String) : LookupResultState
}

data class LookupUiState(
    val activeVehicle: VehicleEntity? = null,
    val codeInput: String = "",
    val resultState: LookupResultState = LookupResultState.Idle,
    val commonCodesQuickList: List<Int> = listOf(16, 24, 35, 68, 79, 82, 84, 89, 95, 128)
)

class ErrorLookupViewModel(
    private val repository: ChevroletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LookupUiState())
    val uiState: StateFlow<LookupUiState> = _uiState.asStateFlow()

    fun loadVehicle(vehicleId: Long) {
        viewModelScope.launch {
            val vehicle = repository.getVehicleById(vehicleId)
            _uiState.update {
                it.copy(
                    activeVehicle = vehicle,
                    codeInput = "",
                    resultState = LookupResultState.Idle
                )
            }
        }
    }

    fun onCodeInputChange(newInput: String) {
        val digitsOnly = newInput.filter { it.isDigit() }.take(4)
        _uiState.update {
            it.copy(
                codeInput = digitsOnly,
                resultState = if (digitsOnly.isEmpty()) LookupResultState.Idle else it.resultState
            )
        }
    }

    fun searchCode(overrideCode: Int? = null) {
        val currentState = _uiState.value
        val vehicle = currentState.activeVehicle ?: return

        val rawText = overrideCode?.toString() ?: currentState.codeInput.trim()
        if (overrideCode != null) {
            _uiState.update { it.copy(codeInput = rawText) }
        }

        val codeNumber = rawText.toIntOrNull()
        if (codeNumber == null) {
            _uiState.update {
                it.copy(resultState = LookupResultState.InvalidInput("Digite um número de código válido (ex: 89)."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(resultState = LookupResultState.Loading) }
            val result = repository.findErrorCodeForVehicle(
                codeNumber = codeNumber,
                vehicleModel = vehicle.model
            )
            _uiState.update {
                if (result != null) {
                    it.copy(resultState = LookupResultState.Found(result))
                } else {
                    it.copy(
                        resultState = LookupResultState.NotFound(
                            searchedCode = codeNumber,
                            vehicleModel = vehicle.model
                        )
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(repository: ChevroletRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ErrorLookupViewModel(repository) as T
                }
            }
    }
}
