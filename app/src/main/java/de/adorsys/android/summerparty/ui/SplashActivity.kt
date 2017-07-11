package de.adorsys.android.summerparty.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(MainActivity.KEY_PREFS_FILENAME, Context.MODE_PRIVATE)

        val intent: Intent
        if ((preferences as SharedPreferences).contains(MainActivity.KEY_USER_NAME)) {
            intent = Intent(this, MainActivity::class.java)
        } else {
            intent = Intent(this, CreateUserActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}