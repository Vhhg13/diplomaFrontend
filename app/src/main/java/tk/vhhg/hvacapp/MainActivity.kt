package tk.vhhg.hvacapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import tk.vhhg.auth.model.TokenPair
import tk.vhhg.auth.ui.LoginScreen
import tk.vhhg.hvacapp.navigation.Navigation
import tk.vhhg.theme.HvacAppTheme
import android.Manifest.permission.POST_NOTIFICATIONS as POST_NOTIFICATIONS

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HvacAppTheme {
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                if (isLoggedIn == null) {
                    Surface { LoginScreen(Modifier.fillMaxSize()) }
                } else if (isLoggedIn != TokenPair("", "")) {
                    Navigation(
                        logout = viewModel::logout,
                        enableNotifications = ::askNotificationPermission,
                        modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) Toast.makeText(this, R.string.notifications_granted, Toast.LENGTH_LONG).show()
            else if (!shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                Toast.makeText(this, R.string.allow_notifications_yourself, Toast.LENGTH_LONG).show()
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return
        println(ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS))
        println(shouldShowRequestPermissionRationale(POST_NOTIFICATIONS))
        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.already_allowed, Toast.LENGTH_LONG).show()
            return
        }
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }
}
