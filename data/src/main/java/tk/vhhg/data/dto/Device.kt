package tk.vhhg.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: Long,
    val name: String,
    val type: DeviceType,
    val roomId: Long,
    val historicData: List<PieceOfHistory>?,
    val topic: String,
    val maxPower: Float
) {
    companion object {
        const val NONEXISTENT_DEVICE_ID = 0L
    }
}