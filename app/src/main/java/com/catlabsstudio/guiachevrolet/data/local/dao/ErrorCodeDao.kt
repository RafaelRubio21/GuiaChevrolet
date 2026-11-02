package com.catlabsstudio.guiachevrolet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.catlabsstudio.guiachevrolet.data.local.entity.ErrorCodeEntity

@Dao
interface ErrorCodeDao {

    @Query("SELECT * FROM error_codes WHERE code_number = :codeNumber")
    suspend fun getErrorCodesByNumber(codeNumber: Int): List<ErrorCodeEntity>

    @Query("SELECT * FROM error_codes ORDER BY code_number ASC")
    suspend fun getAllErrorCodes(): List<ErrorCodeEntity>

    @Query("SELECT COUNT(*) FROM error_codes")
    suspend fun getErrorCodesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(codes: List<ErrorCodeEntity>)
}
