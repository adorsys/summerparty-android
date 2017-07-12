package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.adorsys.android.summerparty.SummerpartyApp

open class BaseActivity : AppCompatActivity() {
    private var app: SummerpartyApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as SummerpartyApp?
    }

    override fun onResume() {
        super.onResume()
        app?.currentActivity = this
    }

    override fun onPause() {
        if (this == app?.currentActivity) {
            app?.currentActivity = null
        }
        super.onPause()
    }
}
