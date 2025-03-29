package tk.vhhg.auth.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import tk.vhhg.auth.di.TokenDataStore
import tk.vhhg.auth.di.TokenServiceCoroutineScope
import tk.vhhg.auth.model.TokenPair
import javax.inject.Inject

class TokenServiceImpl @Inject constructor(
    @TokenDataStore
    private val tokenPreferences: DataStore<Preferences>,
    @TokenServiceCoroutineScope
    tokenServiceCoroutineScope: CoroutineScope,
) : TokenService {

    private val accessKey = stringPreferencesKey("access")
    private val refreshKey = stringPreferencesKey("refresh")

    override val tokenPair = tokenPreferences.data.map { prefs ->
        val access = prefs[accessKey]
        val refresh = prefs[refreshKey]
        if (access != null && refresh != null) TokenPair(access, refresh) else null
    }.stateIn(tokenServiceCoroutineScope, SharingStarted.Eagerly, TokenPair("", ""))

    override suspend fun set(pair: TokenPair?) {
        tokenPreferences.edit { preferences ->
            if (pair == null) {
                preferences.remove(accessKey)
                preferences.remove(refreshKey)
            } else {
                preferences[accessKey] = pair.access
                preferences[refreshKey] = pair.refresh
            }
        }
    }
}