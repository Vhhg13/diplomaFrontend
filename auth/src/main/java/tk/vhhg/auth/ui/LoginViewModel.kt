package tk.vhhg.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.auth.data.AuthService
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SubmitEvent -> submit(event)
            UiEvent.SwitchEvent -> switch()
            UiEvent.FieldsChangedEvent -> onFieldsChange()
        }
    }

    private fun submit(event: UiEvent.SubmitEvent) {
        _uiState.update { it.copy(isLoading = true) }

        if (uiState.value.isLoggingIn) {
            login(event.username, event.password)
        } else if (event.password != event.passwordConfirmation) {
            _uiState.update { it.copy(error = UiError.PASSWORDS_DO_NOT_MATCH, isLoading = false) }
        } else {
            register(event.username, event.password)
        }
    }

    private fun switch() {
        _uiState.update {
            it.copy(isLoggingIn = !it.isLoggingIn, error = null)
        }
    }

    private fun onFieldsChange() {
        _uiState.update { it.copy(error = null) }
    }

    private fun login(username: String, password: String) = viewModelScope.launch {
        if (authService.login(username, password))
            _uiState.update { it.copy(isLoading = false, isLoggingIn = true) }
        else
            _uiState.update { it.copy(error = UiError.WRONG_CREDENTIALS, isLoading = false) }
    }

    private fun register(username: String, password: String) = viewModelScope.launch {
        if (authService.register(username, password))
            _uiState.update { it.copy(isLoading = false, isLoggingIn = true) }
        else
            _uiState.update { it.copy(error = UiError.USERNAME_TAKEN, isLoading = false) }
    }
}