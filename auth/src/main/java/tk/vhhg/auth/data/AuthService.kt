package tk.vhhg.auth.data

import tk.vhhg.auth.model.TokenPair

interface AuthService {
    suspend fun login(username: String, password: String): Boolean
    suspend fun register(username: String, password: String): Boolean
    suspend fun logout()
    suspend fun refresh(access: String, refresh: String): TokenPair?
}