package tk.vhhg.device

import tk.vhhg.data.dto.Device

data class UiState(
    val device: Device,
    val currentWattage: Float = 0F,
    val isLoading: Boolean = true,
    val roomName: String = "",
    val maxPower: String = "0",
    val needsMaxPowerSupportingText: Boolean = false
)