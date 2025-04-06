package tk.vhhg.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PieceOfHistory(
    val time: Long,
    val value: String
)