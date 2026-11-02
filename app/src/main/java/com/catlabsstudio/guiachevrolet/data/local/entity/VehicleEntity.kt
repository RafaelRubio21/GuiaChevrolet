package com.catlabsstudio.guiachevrolet.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val model: String,
    val year: Int,
    val nickname: String? = null,
    @ColumnInfo(name = "manual_url")
    val manualUrl: String = getManualUrlForModel(model)
) {
    val displayTitle: String
        get() = if (!nickname.isNullOrBlank()) "$nickname ($model $year)" else "$model $year"

    companion object {
        val MANUAL_URL_BY_MODEL: Map<String, String> = linkedMapOf(
            "Onix" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/services/owner-manuals/06-pdf/my185-onix-52152046-por-20171110-v0-3-low.pdf",
            "Onix Plus" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/visid/services/manuals/pdfs/onix-plus/01-pdfs/2025/onix-plus-2025-manual-do-propietario.pdf",
            "Prisma" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/services/owner-manuals/06-pdf/my185-prisma-52152050-por-20171110-v0-3-low.pdf",
            "Cobalt" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/services/owner-manuals/06-pdf/cy18-cobalt-52153126-por-20171208-v0-2-baixa.pdf",
            "Spin" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/visid/services/manuals/pdfs/spin/01-pdfs/2025/spin-2025-manual-do-propietario.pdf",
            "Cruze" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/services/owner-manuals/05-pdf/cruze/my17_cruze_52140006_por_20161011_v01.pdf",
            "Tracker" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/visid/services/manuals/pdfs/tracker/01-pdfs/2025/tracker-2025-manual-do-propietario.pdf",
            "Sonic" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/services/owner-manuals/05-pdf/sonic/manual-sonic-2014.pdf",
            "Montana" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/visid/services/manuals/pdfs/montana/01-pdfs/2025/montana-2025-manual-do-propietario.pdf",
            "S10" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/visid/services/manuals/pdfs/s10/01-pdfs/2025/s10-2025-manual-do-propietario.pdf",
            "Trailblazer" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/visid/services/manuals/pdfs/trailblazer/01-pdfs/2025/trailblazer-2025-manual-do-propietario.pdf",
            "Camaro" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/services/owner-manuals/05-pdf/camaro/my17-camaro-98550699-por-20161020.pdf",
            "Equinox" to "https://www.chevrolet.com.br/content/dam/chevrolet/south-america/brazil/portuguese/index/visid/services/manuals/pdfs/equinox/01-pdfs/2024/equinox-2024-manual-do-propietario.pdf"
        )

        val SUPPORTED_GM_MODELS: List<String> = MANUAL_URL_BY_MODEL.keys.toList()

        fun getManualUrlForModel(model: String): String {
            return MANUAL_URL_BY_MODEL[model]
                ?: MANUAL_URL_BY_MODEL.entries.firstOrNull { it.key.equals(model, ignoreCase = true) }?.value
                ?: MANUAL_URL_BY_MODEL.values.first()
        }
    }
}
