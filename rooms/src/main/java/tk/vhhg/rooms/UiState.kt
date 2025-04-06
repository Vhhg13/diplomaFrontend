package tk.vhhg.rooms

import tk.vhhg.data.dto.Room

data class UiState(
    val rooms: List<Room> = emptyList(),
    val needsNameSupportingText: Boolean = false,
    val needsVolSupportingText: Boolean = false,
    val needsColorSupportingText: Boolean = false,
    val isLoading: Boolean = true
) {
    val pageCount: Int get() = rooms.size
}