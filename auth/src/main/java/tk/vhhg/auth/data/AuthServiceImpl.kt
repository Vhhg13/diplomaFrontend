package tk.vhhg.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import tk.vhhg.auth.model.TokenPair
import tk.vhhg.auth.model.UsernamePassword
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val tokenService: TokenService,
    private val ktor: HttpClient,
) : AuthService {

    private suspend fun postCredentialsToGetTokens(
        username: String,
        password: String,
        path: String,
    ): Boolean {
        val response = ktor.post(path) {
            contentType(ContentType.Application.Json)
            setBody(UsernamePassword(username, password))
        }
        if (response.status != HttpStatusCode.OK) return false

        val tokenPair: TokenPair = response.body()
        tokenService.set(tokenPair)
        return true
    }

    override suspend fun login(username: String, password: String): Boolean {
        return postCredentialsToGetTokens(username, password, "/login")
    }

    override suspend fun register(username: String, password: String): Boolean {
        return postCredentialsToGetTokens(username, password, "/register")
    }

    override suspend fun logout() {
        tokenService.set(null)
    }

    override suspend fun refresh(access: String, refresh: String): TokenPair? {
        val response = ktor.post("/refresh") {
            contentType(ContentType.Application.Json)
            setBody(TokenPair(access, refresh))
        }
        if (response.status != HttpStatusCode.OK) return null

        val tokenPair = response.body<TokenPair>()
        tokenService.set(tokenPair)
        return tokenPair
    }
}