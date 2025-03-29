package tk.vhhg.auth.ui

sealed interface UiEvent {
    data class SubmitEvent(
        val username: String,
        val password: String,
        val passwordConfirmation: String = password,
    ) : UiEvent

    data object SwitchEvent : UiEvent
    data object FieldsChangedEvent : UiEvent
}