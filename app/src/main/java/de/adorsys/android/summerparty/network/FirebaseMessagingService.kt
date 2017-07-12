package de.adorsys.android.summerparty.network

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.adorsys.android.summerparty.SummerpartyApp
import de.adorsys.android.summerparty.ui.MainActivity

class FirebaseMessagingService : FirebaseMessagingService() {
    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    // happens when app is in foreground
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        sendBroadcast(remoteMessage?.notification?.body)
    }

    // happens when app is in background
    override fun handleIntent(p0: Intent?) {
        super.handleIntent(p0)
        if ((application as SummerpartyApp).currentActivity == MainActivity::class.java) {
            sendBroadcast()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(MainActivity.KEY_FIREBASE_RELOAD, true)
            startActivity(intent)
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.

     * @param messageBody FCM message body received.
     */
    private fun sendBroadcast(messageBody: String? = null) {
        val intent = Intent(MainActivity.KEY_FIREBASE_RECEIVER)
        intent.putExtra(MainActivity.KEY_FIREBASE_RELOAD, true)
        broadcaster?.sendBroadcast(intent)
    }
}