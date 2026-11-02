package com.catlabsstudio.guiachevrolet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.catlabsstudio.guiachevrolet.data.local.dao.ErrorCodeDao
import com.catlabsstudio.guiachevrolet.data.local.dao.VehicleDao
import com.catlabsstudio.guiachevrolet.data.local.dao.VehicleManualDao
import com.catlabsstudio.guiachevrolet.data.local.entity.ErrorCodeEntity
import com.catlabsstudio.guiachevrolet.data.local.entity.VehicleEntity
import com.catlabsstudio.guiachevrolet.data.local.entity.VehicleManualEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VehicleEntity::class,
        ErrorCodeEntity::class,
        VehicleManualEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun errorCodeDao(): ErrorCodeDao
    abstract fun vehicleManualDao(): VehicleManualDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, applicationScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "guia_chevrolet.db"
                )
                    .addCallback(DatabaseCallback(applicationScope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    database.errorCodeDao().insertAll(PrepopulateData.initialErrorCodes)
                    database.vehicleManualDao().insertAll(PrepopulateData.initialVehicleManuals)
                }
            }
        }
    }
}
