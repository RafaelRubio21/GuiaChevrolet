package com.catlabsstudio.guiachevrolet.data.local

import androidx.room.TypeConverter
import com.catlabsstudio.guiachevrolet.data.local.entity.Severity

class Converters {

    @TypeConverter
    fun fromSeverity(severity: Severity): String = severity.name

    @TypeConverter
    fun toSeverity(value: String): Severity =
        runCatching { Severity.valueOf(value) }.getOrDefault(Severity.ALERTA)

    @TypeConverter
    fun fromModelList(models: List<String>): String =
        models.joinToString(separator = ",") { it.trim() }

    @TypeConverter
    fun toModelList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split(",").map { it.trim() }
}
