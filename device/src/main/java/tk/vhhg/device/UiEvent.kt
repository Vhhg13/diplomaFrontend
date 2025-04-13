package tk.vhhg.device

sealed interface UiEvent {
    data class SetWattageEvent(val wattage: Float) : UiEvent
    data class OnTopicChangedEvent(val topic: String) : UiEvent
    data class OnDeviceNameChangedEvent(val name: String) : UiEvent
    data object OnSaveDeviceEvent : UiEvent
    data class ChangeMaxPowerEvent(val maxPower: String) : UiEvent
    data object SwitchDeviceTypeEvent : UiEvent
}