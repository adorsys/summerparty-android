package de.adorsys.android.summerparty.network

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.adorsys.android.summerparty.R
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
        sendBroadcast()
    }

    // happens when app is in background
    override fun handleIntent(p0: Intent?) {
        sendBroadcast(p0?.getStringExtra("gcm.notification.body"))
    }

    private fun sendBroadcast(body: String? = null, icon: Int = R.drawable.ic_cocktail_icon) {
        val app = (application as SummerpartyApp)
        if (app.currentActivity != null
                && app.currentActivity!!::class.java == MainActivity::class.java) {
            val intent = Intent(MainActivity.KEY_FIREBASE_RECEIVER)
            intent.putExtra(MainActivity.KEY_FIREBASE_RELOAD, true)
            broadcaster?.sendBroadcast(intent)
        } else {
            val notificationBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(icon)
                    .setContentTitle(getString(R.string.notification_content_title))
                    .setContentText(body ?: getString(R.string.notification_content_text))
                    .setAutoCancel(true)

            val resultIntent = Intent(this, MainActivity::class.java)
            resultIntent.putExtra(MainActivity.KEY_FIREBASE_RELOAD, true)
            val resultPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            notificationBuilder.setContentIntent(resultPendingIntent)
            // vibration
            notificationBuilder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000))
            // led
            notificationBuilder.setLights(Color.BLUE, 1000, 500)
            // from Android O on
            // notificationBuilder.enableLights(true)
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            val notificationId = 110
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
}