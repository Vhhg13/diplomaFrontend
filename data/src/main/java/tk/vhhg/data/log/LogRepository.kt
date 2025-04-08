package tk.vhhg.data.log

import java.time.Instant

interface LogRepository {
    suspend fun filter(
        roomId: Long? = null,
        device: Long? = null,
        from: Instant? = null,
        to: Instant? = null,
    ): List<String>
}