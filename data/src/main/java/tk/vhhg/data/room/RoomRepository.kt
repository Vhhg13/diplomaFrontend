package tk.vhhg.data.room

import tk.vhhg.data.dto.Room
import tk.vhhg.data.error.ChangeTempError
import java.time.Instant

interface RoomRepository {
    suspend fun changeTemperature(roomId: Long, target: Float, deadline: Instant?): ChangeTempError?
    suspend fun deleteRoom(roomId: Long): Boolean
    suspend fun getRoomById(roomId: Long): Room?
    suspend fun updateRoom(room: Room): Boolean
    suspend fun getRooms(): List<Room>
    suspend fun createRoom(name: String, volume: Float, color: String): Room
}