package tk.vhhg.data.device

import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import tk.vhhg.data.dto.Device
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(private val client: HttpClient) : DeviceRepository {
    override suspend fun getDevicesIn(roomId: Long): List<Device>? {
        TODO("Not yet implemented")
    }

    override suspend fun createDevice(device: Device): Device? {
        TODO("Not yet implemented")
    }

    override suspend fun updateDevice(device: Device): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getDeviceDataFlow(roomId: Long, deviceId: Long): Flow<Float>? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDevice(deviceId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setDeviceValue(roomId: Long, deviceId: Long, value: Float): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getDeviceWithHistoricData(
        roomId: Long,
        deviceId: Long,
        from: Instant?,
        to: Instant?,
    ): Device? {
        TODO("Not yet implemented")
    }
}