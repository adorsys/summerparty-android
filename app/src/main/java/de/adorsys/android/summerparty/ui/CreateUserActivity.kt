package de.adorsys.android.summerparty.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.ApiManager
import de.adorsys.android.summerparty.data.Customer
import de.adorsys.android.summerparty.data.mutable.MutableCustomer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                ApiManager.INSTANCE.createCustomer(MutableCustomer(usernameEditText.text.toString(), FirebaseInstanceId.getInstance().token),
                        object : Callback<Customer> {
                            override fun onResponse(call: Call<Customer>?, response: Response<Customer>?) {
                                val customer = response?.body()
                                (preferences as SharedPreferences).edit().putString(MainActivity.KEY_USER_ID, customer?.id).apply()
                            }

                            override fun onFailure(call: Call<Customer>?, t: Throwable?) {
                                Log.i("TAG_USER", t?.message)
                            }
                        })
                val intent = Intent(this@CreateUserActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

    }
}
