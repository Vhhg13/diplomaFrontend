package tk.vhhg.hvacapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tk.vhhg.auth.model.TokenPair
import tk.vhhg.auth.ui.LoginScreen
import tk.vhhg.hvacapp.navigation.Navigation
import tk.vhhg.theme.HvacAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HvacAppTheme {
                val viewModel = hiltViewModel<MainActivityViewModel>()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                if (isLoggedIn == null) {
                    Surface { LoginScreen(Modifier.fillMaxSize()) }
                } else if (isLoggedIn != TokenPair("", "")) {
                    Navigation(viewModel::logout, Modifier.fillMaxSize())
                }
            }
        }
    }
}