package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.widget.Button
import de.adorsys.android.summerparty.R

class CreateUserActivity : BaseActivity() {
    companion object {
        const val KEY_USERNAME = "key_username"
    }
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)

        preferences = getSharedPreferences(MainActivity.KEY_PREFS_FILENAME, Context.MODE_PRIVATE)

        setContentView(R.layout.activity_create_user)
        val usernameInputLayout = findViewById<TextInputLayout>(R.id.username_input_layout)
        val usernameEditText = findViewById<TextInputEditText>(R.id.username_edit_text)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener({
            val username = usernameEditText.text.toString()
            if (TextUtils.isEmpty(username)) {
                usernameInputLayout.error = getString(R.string.error_user_not_specified)
                usernameInputLayout.isErrorEnabled = true
            } else {
                val intent = Intent()
                intent.putExtra(KEY_USERNAME, username)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })
    }
}
