package tk.vhhg.hvacapp.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import tk.vhhg.auth.data.TokenService
import tk.vhhg.auth.model.TokenPair
import tk.vhhg.hvacapp.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    companion object {
        private const val BASE_URL = BuildConfig.SERVER_ADDRESS

        @Provides
        fun provideKtor(tokenService: TokenService): HttpClient {
            return HttpClient(CIO) {
                install(ContentNegotiation) { json() }
                defaultRequest { url(BASE_URL) }
                install(Logging)
                install(Auth) {
                    bearer {
                        loadTokens {
                            tokenService.tokenPair.value?.let {
                                BearerTokens(it.access, it.refresh)
                            }
                        }
                        refreshTokens {
                            val (access, refresh) = tokenService.tokenPair.value
                                ?: throw IllegalStateException("Should have had tokens")
                            val response = client.post("/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(TokenPair(access, refresh))
                            }
                            if (response.status != HttpStatusCode.OK) {
                                Log.d("refresh", response.body())
                            }
                            val tokens = try {
                                response.body<TokenPair>()
                            } catch (e: NoTransformationFoundException) {
                                null
                            }
                            tokenService.set(tokens)
                            tokens?.let { BearerTokens(tokens.access, tokens.refresh) }
                        }
                    }
                }
            }
        }
    }
}