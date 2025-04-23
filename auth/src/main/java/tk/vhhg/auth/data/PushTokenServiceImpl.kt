package tk.vhhg.auth.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tk.vhhg.auth.di.TokenDataStore
import tk.vhhg.auth.model.TokenPair
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushTokenServiceImpl @Inject constructor(
    @TokenDataStore
    private val pushTokenDataStore: DataStore<Preferences>,
    jwtTokenService: TokenService,
    client: HttpClient
): PushTokenService {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val pushTokenKey = stringPreferencesKey("push")

    private val stateFlow: StateFlow<String?> = pushTokenDataStore.data.map {
        it[pushTokenKey]
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    private val emptyJwtPair = TokenPair("", "")

    private val combinedFlow = stateFlow.combine(jwtTokenService.tokenPair) { push, jwt ->
        push to jwt
    }

    init {
        println("initializing PushTokenService")
        coroutineScope.launch {
            combinedFlow.collect { (push, jwt) ->
                println(push + jwt)
                if (push != null && jwt != null && jwt != emptyJwtPair) {
                    client.post("push") {
                        contentType(ContentType.Text.Plain)
                        setBody(push)
                    }
                }
            }
        }
    }

    override fun setToken(token: String) {
        coroutineScope.launch {
            pushTokenDataStore.edit {
                it[pushTokenKey] = token
            }
        }
    }
}