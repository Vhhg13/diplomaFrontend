package tk.vhhg.specific_room

import tk.vhhg.data.dto.Device

data class UiState(
    val isLoading: Boolean = true,
    val currentTemp: Float? = null,
    val targetTemp: Float? = currentTemp,
    val deadline: Long? = null,
    val devices: List<Device> = emptyList(),
    val scriptCode: String = ""
)