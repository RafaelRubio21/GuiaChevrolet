package com.guiachevrolet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val model: String,
    val year: Int,
    val nickname: String? = null
) {
    val displayTitle: String
        get() = if (!nickname.isNullOrBlank()) "$nickname ($model $year)" else "$model $year"

    companion object {
        val SUPPORTED_GM_MODELS = listOf(
            "Onix",
            "Onix Plus",
            "Prisma",
            "Cobalt",
            "Spin",
            "Cruze",
            "Tracker",
            "Sonic",
            "Montana",
            "S10",
            "Trailblazer",
            "Camaro",
            "Equinox"
        )
    }
}
