package tk.vhhg.data.room

import tk.vhhg.data.dto.Room
import tk.vhhg.data.error.ChangeTempError

interface RoomRepository {
    suspend fun changeTemperatureRegime(roomId: Long, target: Float?, deadline: Long?): ChangeTempError?
    suspend fun deleteRoom(roomId: Long): Boolean
    suspend fun getRoomById(roomId: Long): Room?
    suspend fun updateRoom(oldRoom: Room, newRoom: Room): Boolean
    suspend fun getRooms(): List<Room>
    suspend fun createRoom(name: String, volume: Float, color: String): Room
}