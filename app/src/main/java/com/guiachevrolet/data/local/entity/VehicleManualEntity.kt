package com.guiachevrolet.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_manuals")
data class VehicleManualEntity(
    @PrimaryKey
    val model: String,
    val title: String,
    @ColumnInfo(name = "pdf_url")
    val pdfUrl: String
)
