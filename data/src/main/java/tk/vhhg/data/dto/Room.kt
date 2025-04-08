package tk.vhhg.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


@Serializable
data class Room(
    @SerialName(ID_SERIAL_NAME)
    val id: Long,
    @SerialName(NAME_SERIAL_NAME)
    val name: String,
    @SerialName(VOLUME_SERIAL_NAME)
    val volume: Float,
    @SerialName(COLOR_SERIAL_NAME)
    val color: String,
    @SerialName(SCRIPT_CODE_SERIAL_NAME)
    val scriptCode: String,
    val devices: List<Device> = emptyList(),
    val deadline: Long? = null,
    val target: Float? = null
) {
    companion object {
        const val ID_SERIAL_NAME = "id"
        const val NAME_SERIAL_NAME = "name"
        const val VOLUME_SERIAL_NAME = "volume"
        const val COLOR_SERIAL_NAME = "color"
        const val SCRIPT_CODE_SERIAL_NAME = "scriptCode"
    }
    fun getPatchInto(newRoom: Room): JsonObject {
        return buildJsonObject {
            put(ID_SERIAL_NAME, id)
            if (name != newRoom.name) put(NAME_SERIAL_NAME, newRoom.name)
            if (volume != newRoom.volume) put(VOLUME_SERIAL_NAME, newRoom.volume)
            if (color != newRoom.color) put(COLOR_SERIAL_NAME, newRoom.color)
            if (scriptCode != newRoom.scriptCode) put(SCRIPT_CODE_SERIAL_NAME, newRoom.scriptCode)
        }
    }
}