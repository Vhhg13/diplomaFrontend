package tk.vhhg.im.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class ImRepositoryImpl @Inject constructor(private val ktor: HttpClient) : ImRepository {

    override suspend fun putIm(im: String) = withContext(Dispatchers.IO) {
        ktor.put("im") {
            contentType(ContentType.Application.Json)
            setBody(im)
        }.apply {
            Log.d("deb", this.bodyAsText())
            Log.d("deb", this.status.toString())
        }
    }.let {}

    override suspend fun getIms(): List<String> = withContext(Dispatchers.IO) {
        ktor.get("im").body<List<JsonObject>>().map(JsonObject::toString)
    }

    override suspend fun deleteIm(id: Int): Boolean = withContext(Dispatchers.IO) {
        val response = ktor.delete("im/$id")
        response.status == HttpStatusCode.OK
    }
}