package de.adorsys.android.summerparty.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
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

    private fun sendBroadcast(body: String? = null, icon: Int = R.drawable.ic_cocktail_icon) {
        val app = (application as SummerpartyApp)

        if (app.currentActivity != null
                && app.currentActivity!!::class.java == MainActivity::class.java) {
            val intent = Intent(MainActivity.KEY_FIREBASE_RECEIVER)
            intent.putExtra(MainActivity.KEY_FIREBASE_RELOAD, true)
            broadcaster?.sendBroadcast(intent)
        } else {
            val notificationBuilder =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = createNotificationChannel()
                        NotificationCompat.Builder(this, channel.id)
                    } else {
                        NotificationCompat.Builder(this)
                    }

            notificationBuilder.setSmallIcon(icon)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): NotificationChannel {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = getString(R.string.channel_name)
        val description = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("cocktails", name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channel
    }
}