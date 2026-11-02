package com.catlabsstudio.guiachevrolet

import android.app.Application
import com.catlabsstudio.guiachevrolet.data.local.AppDatabase
import com.catlabsstudio.guiachevrolet.data.repository.ChevroletRepository
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
