package de.adorsys.android.summerparty.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import de.adorsys.android.summerparty.R

class CreateUserActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        preferences = getPreferences(Context.MODE_PRIVATE)

        val usernameEditText = findViewById(R.id.username_edit_text) as EditText
        val registerButton = findViewById(R.id.register_button) as Button

        registerButton.setOnClickListener({
            if (TextUtils.isEmpty(usernameEditText.text.toString())) {
                Toast.makeText(this@CreateUserActivity, "Cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
//                ApiManager.INSTANCE.createCustomer(MutableCustomer(usernameEditText.text.toString(), ))
//                val editor = preferences!!.edit()
//                editor.putString(MainActivity.KEY_USER_ID, usernameEditText.text.toString())
//                editor.commit()
//                Log.d("LOGTAG", preferences!!.getString(MainActivity.KEY_USER_ID, "123"))
//                val intent = Intent(this@CreateUserActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        })

    }
}
