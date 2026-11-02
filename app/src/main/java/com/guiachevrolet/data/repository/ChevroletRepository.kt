package com.guiachevrolet.data.repository

import com.guiachevrolet.data.local.PrepopulateData
import com.guiachevrolet.data.local.dao.ErrorCodeDao
import com.guiachevrolet.data.local.dao.VehicleDao
import com.guiachevrolet.data.local.entity.ErrorCodeEntity
import com.guiachevrolet.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

class ChevroletRepository(
    private val vehicleDao: VehicleDao,
    private val errorCodeDao: ErrorCodeDao
) {
    val allVehicles: Flow<List<VehicleEntity>> = vehicleDao.observeAllVehicles()

    suspend fun ensureSeeded() {
        if (errorCodeDao.getErrorCodesCount() == 0) {
            errorCodeDao.insertAll(PrepopulateData.initialErrorCodes)
        }
    }

    suspend fun hasRegisteredVehicles(): Boolean {
        return vehicleDao.getVehicleCount() > 0
    }

    suspend fun getVehicleById(id: Long): VehicleEntity? {
        return vehicleDao.getVehicleById(id)
    }

    suspend fun insertVehicle(vehicle: VehicleEntity): Long {
        return vehicleDao.insertVehicle(vehicle)
    }

    suspend fun updateVehicle(vehicle: VehicleEntity) {
        vehicleDao.updateVehicle(vehicle)
    }

    suspend fun deleteVehicle(vehicle: VehicleEntity) {
        vehicleDao.deleteVehicle(vehicle)
    }

    suspend fun findErrorCodeForVehicle(codeNumber: Int, vehicleModel: String): ErrorCodeEntity? {
        ensureSeeded()
        val candidates = errorCodeDao.getErrorCodesByNumber(codeNumber)
        return candidates.firstOrNull { code ->
            code.applicableModels.any { model ->
                model.equals(vehicleModel, ignoreCase = true) || model.equals("ALL", ignoreCase = true)
            }
        }
    }

    suspend fun getAllErrorCodes(): List<ErrorCodeEntity> {
        ensureSeeded()
        return errorCodeDao.getAllErrorCodes()
    }
}
