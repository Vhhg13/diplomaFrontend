package tk.vhhg.specific_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.data.dto.DeviceType
import tk.vhhg.data.room.RoomRepository

@HiltViewModel(assistedFactory = SpecificRoomViewModel.Factory::class)
class SpecificRoomViewModel @AssistedInject constructor(
    @Assisted private val roomId: Long,
    private val roomRepository: RoomRepository
) : ViewModel() {
    companion object {
        const val DEBOUNCE_DURATION = 2000L
    }

    @AssistedFactory
    interface Factory { fun create(roomId: Long): SpecificRoomViewModel }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private val room = viewModelScope.async { roomRepository.getRoomById(roomId)!! }

    init {
        viewModelScope.launch {
            val awaitedRoom = room.await()
            _uiState.update { it.copy(
                scriptCode = awaitedRoom.scriptCode,
                deadline = awaitedRoom.deadline,
                targetTemp = awaitedRoom.target,
                devices = awaitedRoom.devices
            ) }

            val thermostat = uiState.value.devices.getOrNull(0)?.let { firstDevice ->
                if (firstDevice.type == DeviceType.TEMP) firstDevice else null
            }
            //thermostat.getDeviceDataFlow(roomId, firstDevice.id)
            val thermostatDataFlow = flow {
                for (i in 0..100) {
                    emit((18 + i%3).toFloat())
                    delay(1000)
                }
            }
            _uiState.update { it.copy(isLoading = false) }
            thermostatDataFlow.collect { temp ->
                _uiState.update { it.copy(currentTemp = temp) }
            }
        }
    }


    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SaveScriptEvent -> saveScript(event.code)
            is UiEvent.SetDeadlineEvent -> setDeadline(event.deadline)
            is UiEvent.SetTargetTemp -> setTargetTemp(event.temp)
        }
    }


    private fun setTargetTemp(temp: Float) {
        _uiState.update { it.copy(targetTemp = temp) }
        debounce()
    }

    private fun setDeadline(deadline: Long?) {
        _uiState.update { it.copy(deadline = deadline) }
        debounce()
    }


    private var debounceJob: Job? = null
    private fun debounce() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(DEBOUNCE_DURATION)
            val state = uiState.value
            if (state.targetTemp != null && state.targetTemp != state.currentTemp) {
                roomRepository.changeTemperatureRegime(
                    roomId,
                    state.targetTemp,
                    state.deadline
                )
            }
            debounceJob = null
        }
    }

    private fun saveScript(code: String) = viewModelScope.launch {
        _uiState.update { it.copy(scriptCode = code) }
        roomRepository.updateRoom(room.await(), room.await().copy(scriptCode = code))
    }.let {}

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCleared() {
        debounceJob?.cancel()
        val state = uiState.value
        if (state.targetTemp != null && state.targetTemp != state.currentTemp) {
            GlobalScope.launch(Dispatchers.IO) {
                roomRepository.changeTemperatureRegime(
                    roomId,
                    state.targetTemp,
                    state.deadline
                )
            }
        }
        super.onCleared()
    }
}