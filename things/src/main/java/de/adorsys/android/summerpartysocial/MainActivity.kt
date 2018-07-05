package de.adorsys.android.summerpartysocial

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val feedFragment = FeedFragment()
        val orderStateFragment = OrderStateFragment()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_start, feedFragment)
                .replace(R.id.container_end, orderStateFragment)
                .commit()
    }
}
