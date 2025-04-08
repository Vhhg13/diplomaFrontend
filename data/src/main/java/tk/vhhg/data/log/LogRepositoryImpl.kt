package tk.vhhg.data.log

import io.ktor.client.HttpClient
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepositoryImpl @Inject constructor(private val client: HttpClient) : LogRepository {
    override suspend fun filter(roomId: Long?, device: Long?, from: Instant?, to: Instant?): List<String> {
        TODO("Not yet implemented")
    }
}