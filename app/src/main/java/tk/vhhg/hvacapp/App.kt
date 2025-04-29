package tk.vhhg.hvacapp

import android.app.Application
import android.app.UiModeManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import tk.vhhg.auth.data.PushTokenService
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var pushTokenService: PushTokenService
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(UI_MODE_SERVICE) as UiModeManager).setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}