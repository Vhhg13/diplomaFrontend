package tk.vhhg.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DeviceType {
    @SerialName("cond")
    COND,
    @SerialName("temp")
    TEMP,
    @SerialName("heat")
    HEAT
}