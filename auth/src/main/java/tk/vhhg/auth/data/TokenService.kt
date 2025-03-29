package tk.vhhg.auth.data

import kotlinx.coroutines.flow.StateFlow
import tk.vhhg.auth.model.TokenPair

interface TokenService {
    suspend fun set(pair: TokenPair?)
    val tokenPair: StateFlow<TokenPair?>
}