package tk.vhhg.data.device

import kotlinx.coroutines.flow.Flow
import tk.vhhg.data.dto.Device
import java.time.Instant

interface DeviceRepository {
    suspend fun getDevicesIn(roomId: Long): List<Device>?
    suspend fun createDevice(device: Device): Long?
    suspend fun updateDevice(device: Device): Boolean
    suspend fun getDeviceDataFlow(roomId: Long, deviceId: Long): Flow<Float>?
    suspend fun deleteDevice(roomId: Long, deviceId: Long): Boolean
    suspend fun setDeviceValue(roomId: Long, deviceId: Long, value: Float): Boolean
    suspend fun getDeviceWithHistoricData(roomId: Long, deviceId: Long, from: Instant?, to: Instant?): Device?
}