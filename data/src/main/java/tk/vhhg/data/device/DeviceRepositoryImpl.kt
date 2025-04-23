package tk.vhhg.data.device

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import tk.vhhg.data.dto.Device
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class DeviceRepositoryImpl @Inject constructor(private val client: HttpClient) : DeviceRepository {
    override suspend fun getDevicesIn(roomId: Long): List<Device> = TODO("Not needed")

    override suspend fun createDevice(device: Device): Long? = withContext(Dispatchers.IO) {
        val response = client.post("rooms/${device.roomId}/devices") {
            contentType(ContentType.Application.Json)
            setBody(device.copy(historicData = null))
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<Device>().id
        } else {
            null
        }
    }

    override suspend fun updateDevice(device: Device): Boolean = withContext(Dispatchers.IO) {
        val response = client.put("rooms/${device.roomId}/devices") {
            contentType(ContentType.Application.Json)
            setBody(device.copy(historicData = null))
        }
        response.status == HttpStatusCode.OK
    }

    override suspend fun getDeviceDataFlow(roomId: Long, deviceId: Long): Flow<Float> {
        val url = "rooms/$roomId/devices/$deviceId/live"
        return flow {
            client.webSocket(url) {
                for (msg in incoming) {
                    Log.d("ws", "emitted ${msg.data}")
                    emit((String(msg.data).toFloatOrNull()?.times(10) ?: 0F).roundToInt()/10F)
                }
            }
        }
    }

    override suspend fun deleteDevice(
        roomId: Long,
        deviceId: Long,
    ): Boolean = withContext(Dispatchers.IO) {
        val response = client.delete("rooms/$roomId/devices/$deviceId")
        response.status == HttpStatusCode.OK
    }

    override suspend fun setDeviceValue(
        roomId: Long,
        deviceId: Long,
        value: Float,
    ): Boolean = withContext(Dispatchers.IO) {
        val url = "rooms/$roomId/devices/$deviceId?value=$value"
        println(url)
        val response = client.post(url)
        println(response.bodyAsText())
        response.status == HttpStatusCode.OK
    }

    override suspend fun getDeviceWithHistoricData(
        roomId: Long,
        deviceId: Long,
        from: Instant?,
        to: Instant?,
    ): Device? = withContext(Dispatchers.IO) {
        val queryString = buildString {
            if (from != null || to != null) append('?')
            from?.toEpochMilli()?.let { append("from=$it") }
            if (this.isNotEmpty()) append('&')
            to?.toEpochMilli()?.let { append("to=$it") }
        }
        val response = client.get("rooms/$roomId/devices/$deviceId$queryString")
        Log.d("getdivice", response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            null
        }
    }
}