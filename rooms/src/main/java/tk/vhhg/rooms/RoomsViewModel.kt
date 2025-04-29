package tk.vhhg.rooms

import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.data.room.RoomRepository
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel @Inject constructor(private val roomRepository: RoomRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private fun checkValues(name: String, vol: String, clr: String): Boolean {
        if (name.isBlank()) _uiState.update {
            it.copy(needsNameSupportingText = true)
        }
        val volume = vol.toFloatOrNull()
        if (volume == null) _uiState.update {
            it.copy(needsVolSupportingText = true)
        }
        val color = try { clr.toColorInt() } catch (e: IllegalArgumentException) { null }
        if (color == null) _uiState.update {
            it.copy(needsColorSupportingText = true)
        }
        return name.isNotBlank() && color != null && volume != null
    }

    fun update(roomIndex: Int, name: String, vol: String, clr: String): Boolean {
        if (!checkValues(name, vol, clr)) return false
        viewModelScope.launch {
            val oldRoom = uiState.value.rooms[roomIndex]
            val newRoom = oldRoom.copy(
                name = name,
                color = clr,
                volume = vol.toFloat()
            )
            if (!roomRepository.updateRoom(oldRoom, newRoom)) return@launch
            _uiState.update {
                it.copy(rooms = it.rooms.mapIndexed { idx, room ->
                    if (idx == roomIndex) newRoom else room
                })
            }
        }
        return true
    }

    fun add(name: String, vol: String, clr: String): Boolean {
        if (!checkValues(name, vol, clr)) return false
        viewModelScope.launch {
            _uiState.update {
                it.copy(rooms = it.rooms + listOf(roomRepository.createRoom(name, vol.toFloat(), clr)))
            }
        }
        return true
    }

    fun onChange() = _uiState.update {
        it.copy(
            needsNameSupportingText = false,
            needsColorSupportingText = false,
            needsVolSupportingText = false
        )
    }

    fun onDelete(id: Long) = viewModelScope.launch {
        roomRepository.deleteRoom(id)
        _uiState.update {
            it.copy(rooms = it.rooms.filter { room -> room.id != id })
        }
    }.let{}

    fun getData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(rooms = roomRepository.getRooms(), isLoading = false)
            }
        }
    }
    init { getData() }
}