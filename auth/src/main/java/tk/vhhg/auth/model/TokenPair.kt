package tk.vhhg.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenPair(
    val access: String,
    val refresh: String,
)