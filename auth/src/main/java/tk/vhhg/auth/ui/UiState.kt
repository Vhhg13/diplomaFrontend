package tk.vhhg.auth.ui

data class UiState(
    val isLoggingIn: Boolean = true,
    val error: UiError? = null,
    val isLoading: Boolean = false
)

enum class UiError {
    PASSWORDS_DO_NOT_MATCH,
    WRONG_CREDENTIALS,
    USERNAME_TAKEN
}