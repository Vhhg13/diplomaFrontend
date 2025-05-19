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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
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

    companion object { const val DEBOUNCE_DELAY = 1000L }

    @AssistedFactory
    interface Factory { fun create(roomId: Long): SpecificRoomViewModel }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private lateinit var room: Room
    private var collectionJob: Job? = null
    private var debounceJob: Job? = null

    val _snackbarFlow = MutableStateFlow<Boolean?>(null)
    val snackbarFlow = _snackbarFlow.asStateFlow()

    init { getData() }


    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SaveScriptEvent -> saveScript(event.code)
            is UiEvent.SetDeadlineEvent -> setDeadline(event.deadline)
            is UiEvent.SetTargetTemp -> setTargetTemp(event.temp)
            UiEvent.UpdateDataEvent -> getData()
            is UiEvent.DeleteEvent -> deleteDevice(event.deviceId)
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
                        _uiState.update { it.copy(
                            currentTemp = temp,
                            targetTemp = if (temp == it.targetTemp) null else it.targetTemp
                        ) }
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
                    _uiState.update { it.copy(
                        currentTemp = temp,
                        targetTemp = if (temp == it.targetTemp) null else it.targetTemp
                    ) }
                }
            }
        }
    }

    private fun checkRequiredDevices(temp: Float): Pair<Float?, Long?> {
        val requiredType = uiState.value.currentTemp?.let { if (temp > it) DeviceType.HEAT else DeviceType.COND }
        if (uiState.value.devices.firstOrNull { it.type == requiredType } == null) {
            viewModelScope.launch {
                _snackbarFlow.value = requiredType == DeviceType.HEAT
                delay(2000)
                _snackbarFlow.value = null
            }
            return null to null
        }
        return temp to uiState.value.deadline
    }


    private fun setTargetTemp(temp: Float) {
        val (t, deadline) = checkRequiredDevices(temp)
        debounceJob?.cancel()
        _uiState.update { it.copy(targetTemp = t) }
        debounceJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY)
            roomRepository.changeTemperatureRegime(
                roomId = roomId,
                target = t,
                deadline = deadline
            )
            debounceJob = null
        }
    }

    private fun setDeadline(deadline: Long?) {
        _uiState.update { it.copy(deadline = deadline) }
        if (uiState.value.targetTemp == null) return
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY)
            roomRepository.changeTemperatureRegime(
                roomId = roomId,
                target = uiState.value.targetTemp,
                deadline = uiState.value.deadline
            )
            debounceJob = null
        }
    }

    private fun saveScript(code: String) = viewModelScope.launch {
        _uiState.update { it.copy(scriptCode = code) }
        val newRoom = room.copy(scriptCode = code)
        roomRepository.updateRoom(room, newRoom)
        room = newRoom
    }.let {}

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCleared() {
        debounceJob?.let {
            it.cancel()
            GlobalScope.launch(Dispatchers.IO) {
                roomRepository.changeTemperatureRegime(
                    roomId = roomId,
                    target = uiState.value.targetTemp,
                    deadline = uiState.value.deadline
                )
            }
        }
        super.onCleared()
    }
}