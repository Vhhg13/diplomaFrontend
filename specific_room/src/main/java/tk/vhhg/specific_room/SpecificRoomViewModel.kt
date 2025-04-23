package tk.vhhg.specific_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.data.device.DeviceRepository
import tk.vhhg.data.dto.DeviceType
import tk.vhhg.data.dto.Room
import tk.vhhg.data.room.RoomRepository

@HiltViewModel(assistedFactory = SpecificRoomViewModel.Factory::class)
class SpecificRoomViewModel @AssistedInject constructor(
    @Assisted private val roomId: Long,
    private val roomRepository: RoomRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory { fun create(roomId: Long): SpecificRoomViewModel }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private lateinit var room: Room
    private var collectionJob: Job? = null

    init { getData() }


    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SaveScriptEvent -> saveScript(event.code)
            is UiEvent.SetDeadlineEvent -> setDeadline(event.deadline)
            is UiEvent.SetTargetTemp -> setTargetTemp(event.temp)
            UiEvent.UpdateDataEvent -> getData()
            is UiEvent.DeleteEvent -> deleteDevice(event.deviceId)
            UiEvent.ClearEvent -> clearRegime()
            UiEvent.SaveRegimeEvent -> saveRegime()
        }
    }

    private fun saveRegime() {
        viewModelScope.launch {
            val state = uiState.value
            roomRepository.changeTemperatureRegime(
                roomId,
                state.targetTemp,
                state.deadline
            )
        }
    }

    private fun clearRegime() {
        _uiState.update {
            it.copy(
                targetTemp = null,
                deadline = null
            )
        }
        viewModelScope.launch {
            roomRepository.changeTemperatureRegime(roomId, null, null)
        }
    }

    private fun deleteDevice(deviceId: Long) {
        viewModelScope.launch {
            deviceRepository.deleteDevice(roomId, deviceId)
            _uiState.update {
                it.copy(devices = it.devices.filterNot { dev -> dev.id == deviceId })
            }
            val anotherTemp = uiState.value.devices.find { it.type == DeviceType.TEMP }
            if (anotherTemp == null || anotherTemp.id != deviceId) {
                collectionJob?.cancel()
            }
            if (anotherTemp == null) {
                _uiState.update {
                    it.copy(currentTemp = null)
                }
            }
            anotherTemp?.id?.let {
                collectionJob = viewModelScope.launch {
                    deviceRepository.getDeviceDataFlow(roomId, it)?.collect { temp ->
                        _uiState.update { it.copy(currentTemp = temp) }
                    }
                }
            }
        }
    }

    private fun getData() {
        collectionJob?.cancel()
        viewModelScope.launch {
            val awaitedRoom = roomRepository.getRoomById(roomId)!!
            room = awaitedRoom
            _uiState.update { it.copy(
                scriptCode = awaitedRoom.scriptCode,
                deadline = awaitedRoom.deadline,
                targetTemp = awaitedRoom.target,
                devices = awaitedRoom.devices.sortedBy { device -> device.id }
            ) }

            val thermostatDataFlow = uiState.value.devices.find { it.type == DeviceType.TEMP }?.let { firstDevice ->
                if (firstDevice.type == DeviceType.TEMP) firstDevice else {
                    _uiState.update {
                        it.copy(currentTemp = null)
                    }
                    null
                }
            }?.let { thermostat ->
                deviceRepository.getDeviceDataFlow(roomId, thermostat.id)
            }

            _uiState.update { it.copy(isLoading = false) }
            collectionJob = viewModelScope.launch {
                thermostatDataFlow?.collect { temp ->
                    _uiState.update { it.copy(currentTemp = temp) }
                }
            }
        }
    }


    private fun setTargetTemp(temp: Float?) {
        if (temp == uiState.value.currentTemp)
            _uiState.update { it.copy(targetTemp = null) }
        else
            _uiState.update { it.copy(targetTemp = temp) }
    }

    private fun setDeadline(deadline: Long?) {
        _uiState.update { it.copy(deadline = deadline) }
    }

    private fun saveScript(code: String) = viewModelScope.launch {
        _uiState.update { it.copy(scriptCode = code) }
        val newRoom = room.copy(scriptCode = code)
        roomRepository.updateRoom(room, newRoom)
        room = newRoom
    }.let {}

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCleared() {
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