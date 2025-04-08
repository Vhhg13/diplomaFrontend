package tk.vhhg.data.room

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tk.vhhg.data.dto.Room
import tk.vhhg.data.error.ChangeTempError
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(private val client: HttpClient) : RoomRepository {
    override suspend fun changeTemperature(
        roomId: Long,
        target: Float,
        deadline: Instant?,
    ): ChangeTempError? = withContext(Dispatchers.IO) {
        val response = client.post("rooms/$roomId/temperature") {
            contentType(ContentType.Application.Json)
            setBody(buildMap {
                deadline?.let { put("deadline", deadline.epochSecond) }
                put("target", target.toString())
            })
        }
        when (response.status) {
            HttpStatusCode.BadRequest -> ChangeTempError.VALUES_NOT_IN_RANGE
            HttpStatusCode.NotFound -> ChangeTempError.ROOM_NOT_FOUND
            else -> null
        }
    }

    override suspend fun deleteRoom(roomId: Long): Boolean = withContext(Dispatchers.IO) {
        client.delete("rooms/$roomId").status == HttpStatusCode.OK
    }

    override suspend fun getRoomById(roomId: Long): Room? = withContext(Dispatchers.IO) {
        val response = client.get("rooms/$roomId")
        if (response.status == HttpStatusCode.NotFound) null
        else response.body()
    }

    override suspend fun updateRoom(room: Room): Boolean = withContext(Dispatchers.IO) {
        val response = client.patch("rooms") {
            contentType(ContentType.Application.Json)
            setBody(room)
        }
        response.status == HttpStatusCode.OK
    }

    override suspend fun getRooms(): List<Room> = withContext(Dispatchers.IO) {
        client.get("rooms").body()
    }

    override suspend fun createRoom(name: String, volume: Float, color: String): Room = withContext(Dispatchers.IO) {
        client.post("rooms") {
            contentType(ContentType.Application.Json)
            setBody(Room(0, name, volume, color, ""))
        }.body()
    }
}