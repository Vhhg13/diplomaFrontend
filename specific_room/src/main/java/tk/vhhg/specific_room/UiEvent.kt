package tk.vhhg.specific_room

sealed interface UiEvent {
    data class SaveScriptEvent(val code: String) : UiEvent
    data class SetDeadlineEvent(val deadline: Long?) : UiEvent
    data class SetTargetTemp(val temp: Float?) : UiEvent
    data object UpdateDataEvent : UiEvent
    data class DeleteEvent(val deviceId: Long) : UiEvent
}
