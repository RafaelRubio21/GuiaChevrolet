package com.guiachevrolet

import android.app.Application
import com.guiachevrolet.data.local.AppDatabase
import com.guiachevrolet.data.repository.ChevroletRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GuiaChevroletApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this, applicationScope)
    }

    val repository: ChevroletRepository by lazy {
        ChevroletRepository(
            vehicleDao = database.vehicleDao(),
            errorCodeDao = database.errorCodeDao(),
            vehicleManualDao = database.vehicleManualDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.ensureSeeded()
        }
    }
}
