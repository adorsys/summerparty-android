package de.adorsys.android.summerparty.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.SummerpartyApp
import de.adorsys.android.summerparty.ui.MainActivity


class FirebaseMessagingService : FirebaseMessagingService() {
    private var broadcaster: androidx.localbroadcastmanager.content.LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()
        broadcaster = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
    }

    // happens when app is in foreground
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        sendBroadcast()
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(newToken: String?) {
        Log.d("TAG_FIREBASE", "Refreshed token: $newToken")
        newToken?.let { sendRegistrationToServer(it) }
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
            notificationBuilder.setVibrate(longArrayOf(1000, 1000, 1000, 1000))
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

    private fun sendRegistrationToServer(token: String) {
        if ((application as SummerpartyApp).currentActivity == MainActivity::class.java) {
            val intent = Intent(MainActivity.KEY_FIREBASE_RECEIVER)
            intent.putExtra(MainActivity.KEY_FIREBASE_TOKEN, token)
            broadcaster?.sendBroadcast(intent)
        } else {
            // TODO bad, don't --> starts application when it is in background
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(MainActivity.KEY_FIREBASE_TOKEN, token)
            startActivity(intent)
        }
    }
}