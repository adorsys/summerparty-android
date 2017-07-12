package de.adorsys.android.summerparty.network

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import de.adorsys.android.summerparty.SummerpartyApp
import de.adorsys.android.summerparty.ui.MainActivity


class FirebaseInstanceIDService : FirebaseInstanceIdService() {
    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("TAG_FIREBASE", "Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
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