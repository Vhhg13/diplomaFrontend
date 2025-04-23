package tk.vhhg.auth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import tk.vhhg.auth.data.AuthService
import tk.vhhg.auth.data.AuthServiceImpl
import tk.vhhg.auth.data.PushTokenService
import tk.vhhg.auth.data.PushTokenServiceImpl
import tk.vhhg.auth.data.TokenService
import tk.vhhg.auth.data.TokenServiceImpl
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    fun bindTokenService(tokenService: TokenServiceImpl): TokenService

    @Binds
    fun bindAuthService(authService: AuthServiceImpl): AuthService

    @Binds
    fun bindPushTokenService(pushTokenServiceImpl: PushTokenServiceImpl): PushTokenService

    companion object {
        @Provides
        @TokenDataStore
        fun provideTokenDataStore(@ApplicationContext context: Context) = context.tokenDataStore

        @Provides
        @TokenServiceCoroutineScope
        fun provideTokenServiceCoroutineScope() = CoroutineScope(Dispatchers.IO)
    }
}

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "tokens")

@Qualifier
annotation class TokenDataStore

@Qualifier
annotation class TokenServiceCoroutineScope