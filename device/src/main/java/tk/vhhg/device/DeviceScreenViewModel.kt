package tk.vhhg.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.data.device.DeviceRepository
import tk.vhhg.data.dto.Device
import tk.vhhg.data.dto.DeviceType
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltViewModel(assistedFactory = DeviceScreenViewModel.Factory::class)
class DeviceScreenViewModel @AssistedInject constructor(
    @Assisted("roomId") private val roomId: Long,
    @Assisted("deviceId") private val deviceId: Long,
    @Assisted private val roomName: String,
    private val deviceRepository: DeviceRepository,
) : ViewModel() {

    companion object {
        const val DEBOUNCE_DELAY = 500L
    }

    @AssistedFactory
    interface Factory { fun create(@Assisted("roomId") roomId: Long, @Assisted("deviceId") deviceId: Long, roomName: String): DeviceScreenViewModel }

    private val _uiState = MutableStateFlow(UiState(
        Device(
            id = deviceId,
            name = "",
            type = DeviceType.TEMP,
            roomId = roomId,
            historicData = emptyList(),
            topic = "",
            maxPower = 0F
        ),
        roomName = roomName
    ))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            deviceRepository.getDeviceDataFlow(roomId, deviceId)?.collect { deviceValue ->
                _uiState.update {
                    it.copy(currentWattage = deviceValue)
                }
            }
        }
        viewModelScope.launch {
            if (deviceId != Device.NONEXISTENT_DEVICE_ID) {
                deviceRepository.getDeviceWithHistoricData(
                    roomId,
                    deviceId,
                    Instant.now().minus(1, ChronoUnit.MINUTES),
                    Instant.now()
                )?.let { device ->
                    _uiState.update { it.copy(device = device, maxPower = device.maxPower.toString()) }
                }
            }
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }



    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SetWattageEvent -> setWattage(event.wattage)
            UiEvent.OnSaveDeviceEvent -> saveDevice()
            is UiEvent.OnTopicChangedEvent -> onTopicChange(event.topic)
            is UiEvent.OnDeviceNameChangedEvent -> onDeviceNameChanged(event.name)
            is UiEvent.ChangeMaxPowerEvent -> onChangeMaxPower(event.maxPower)
            UiEvent.SwitchDeviceTypeEvent -> switchDeviceType()
        }
    }

    private fun switchDeviceType() {
        val nextType = uiState.value.device.type.ordinal + 1
        val typesAmount = DeviceType.entries.size
        _uiState.update {
            it.copy(device = it.device.copy(type = DeviceType.entries[nextType%typesAmount]))
        }
    }

    private fun onChangeMaxPower(maxPower: String) {
        _uiState.update {
            it.copy(maxPower = maxPower, needsMaxPowerSupportingText = false)
        }
    }

    private fun onDeviceNameChanged(name: String) {
        _uiState.update {
            it.copy(device = it.device.copy(name = name))
        }
    }

    private fun onTopicChange(topic: String) {
        _uiState.update {
            it.copy(device = it.device.copy(topic = topic))
        }
    }

    private fun saveDevice() {
        if (uiState.value.device.type == DeviceType.TEMP) _uiState.update {
            it.copy(device = it.device.copy(maxPower = 0F))
        }
        val maxPowerOrNull = uiState.value.maxPower.toFloatOrNull()
        if (maxPowerOrNull == null) {
            _uiState.update {
                it.copy(needsMaxPowerSupportingText = true)
            }
            return
        }
        viewModelScope.launch {
            val currentDevice = uiState.value.device.copy(maxPower = maxPowerOrNull)
            if (deviceId == Device.NONEXISTENT_DEVICE_ID) {
                deviceRepository.createDevice(currentDevice)
            } else {
                deviceRepository.updateDevice(currentDevice.copy(historicData = null))
            }
        }
    }

    private var valueDebounce: Job? = null
    private fun setWattage(wattage: Float) {
        valueDebounce?.cancel()
        valueDebounce = viewModelScope.launch {
            _uiState.update {
                it.copy(currentWattage = wattage)
            }
            delay(DEBOUNCE_DELAY)
            deviceRepository.setDeviceValue(roomId = roomId, value = wattage, deviceId = deviceId)
            valueDebounce = null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCleared() {
        valueDebounce?.cancel()
        GlobalScope.launch {
            deviceRepository.setDeviceValue(roomId = roomId, value = _uiState.value.currentWattage, deviceId = deviceId)
        }
        super.onCleared()
    }
}