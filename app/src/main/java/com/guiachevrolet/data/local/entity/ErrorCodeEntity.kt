package com.guiachevrolet.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "error_codes",
    indices = [Index(value = ["code_number"])]
)
data class ErrorCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "code_number")
    val codeNumber: Int,
    val title: String,
    val description: String,
    val severity: Severity,
    @ColumnInfo(name = "applicable_models")
    val applicableModels: List<String>
)
