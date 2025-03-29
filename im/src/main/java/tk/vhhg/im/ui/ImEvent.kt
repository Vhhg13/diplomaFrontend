package tk.vhhg.im.ui

sealed interface ImEvent {
    data class SubmitImEvent(val index: Int, val json: String) : ImEvent
    data object AddNewImEvent : ImEvent
    data class DeleteImEvent(val index: Int) : ImEvent
}