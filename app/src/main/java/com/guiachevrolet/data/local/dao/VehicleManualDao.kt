package com.guiachevrolet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guiachevrolet.data.local.entity.VehicleManualEntity

@Dao
interface VehicleManualDao {

    @Query("SELECT * FROM vehicle_manuals WHERE LOWER(model) = LOWER(:model) LIMIT 1")
    suspend fun getManualByModel(model: String): VehicleManualEntity?

    @Query("SELECT COUNT(*) FROM vehicle_manuals")
    suspend fun getManualsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(manuals: List<VehicleManualEntity>)
}
