package tk.vhhg.hvacapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import tk.vhhg.auth.data.PushTokenService
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {
    @Inject
    lateinit var tokenService: PushTokenService
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println(token)
        tokenService.setToken(token)
    }
}