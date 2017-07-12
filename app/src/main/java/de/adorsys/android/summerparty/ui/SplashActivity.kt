package de.adorsys.android.summerparty.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.iid.FirebaseInstanceId
import de.adorsys.android.summerparty.R

class SplashActivity : BaseActivity() {
    companion object {
        val KEY_FIRST_START = "first_start"
    }

    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO get rid of splash screen
        setContentView(R.layout.activity_splash)

        preferences = getSharedPreferences(MainActivity.KEY_PREFS_FILENAME, Context.MODE_PRIVATE)

        if ((preferences as SharedPreferences).contains(MainActivity.KEY_USER_NAME)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (!firstStart() && TextUtils.isEmpty(FirebaseInstanceId.getInstance().token)) {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun firstStart(): Boolean {
        if ((preferences as SharedPreferences).contains(KEY_FIRST_START)) {
            return false
        } else {
            preferences!!.edit().putBoolean(KEY_FIRST_START, true).apply()
            return true
        }
    }
}