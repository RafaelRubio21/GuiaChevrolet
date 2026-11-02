package com.guiachevrolet.data.repository

import com.guiachevrolet.data.local.PrepopulateData
import com.guiachevrolet.data.local.dao.ErrorCodeDao
import com.guiachevrolet.data.local.dao.VehicleDao
import com.guiachevrolet.data.local.dao.VehicleManualDao
import com.guiachevrolet.data.local.entity.ErrorCodeEntity
import com.guiachevrolet.data.local.entity.VehicleEntity
import com.guiachevrolet.data.local.entity.VehicleManualEntity
import kotlinx.coroutines.flow.Flow

class ChevroletRepository(
    private val vehicleDao: VehicleDao,
    private val errorCodeDao: ErrorCodeDao,
    private val vehicleManualDao: VehicleManualDao
) {
    val allVehicles: Flow<List<VehicleEntity>> = vehicleDao.observeAllVehicles()

    suspend fun ensureSeeded() {
        if (errorCodeDao.getErrorCodesCount() == 0) {
            errorCodeDao.insertAll(PrepopulateData.initialErrorCodes)
        }
        if (vehicleManualDao.getManualsCount() == 0) {
            vehicleManualDao.insertAll(PrepopulateData.initialVehicleManuals)
        }
    }

    suspend fun hasRegisteredVehicles(): Boolean {
        return vehicleDao.getVehicleCount() > 0
    }

    suspend fun getVehicleById(id: Long): VehicleEntity? {
        return vehicleDao.getVehicleById(id)
    }

    suspend fun getManualForModel(model: String): VehicleManualEntity {
        ensureSeeded()
        return vehicleManualDao.getManualByModel(model)
            ?: VehicleManualEntity(
                model = model,
                title = "Manual do Proprietário Chevrolet $model",
                pdfUrl = VehicleEntity.getManualUrlForModel(model)
            )
    }

    suspend fun insertVehicle(vehicle: VehicleEntity): Long {
        val manual = getManualForModel(vehicle.model)
        return vehicleDao.insertVehicle(vehicle.copy(manualUrl = manual.pdfUrl))
    }

    suspend fun updateVehicle(vehicle: VehicleEntity) {
        val manual = getManualForModel(vehicle.model)
        vehicleDao.updateVehicle(vehicle.copy(manualUrl = manual.pdfUrl))
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
