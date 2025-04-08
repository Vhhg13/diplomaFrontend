package tk.vhhg.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class Room(
    val id: Long,
    val name: String,
    val volume: Float,
    val color: String,
    val scriptCode: String,
    val devices: List<Device> = emptyList(),
    val deadline: Long? = null,
    val target: Float? = null
)