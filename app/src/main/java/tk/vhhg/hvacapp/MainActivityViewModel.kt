package tk.vhhg.hvacapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tk.vhhg.auth.data.TokenService
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val tokenService: TokenService,
) : ViewModel() {
    val isLoggedIn = tokenService.tokenPair
    fun logout() {
        viewModelScope.launch {
            tokenService.set(null)
        }
    }
}