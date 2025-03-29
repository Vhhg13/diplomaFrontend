package tk.vhhg.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UsernamePassword(
    val username: String,
    val password: String,
)