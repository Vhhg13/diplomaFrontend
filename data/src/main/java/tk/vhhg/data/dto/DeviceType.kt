package tk.vhhg.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DeviceType {
    @SerialName("temp")
    TEMP,
    @SerialName("cond")
    COND,
    @SerialName("heat")
    HEAT
}