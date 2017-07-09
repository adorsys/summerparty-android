package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.ApiManager
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import de.adorsys.android.summerparty.ui.views.OrderView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity() {
    companion object {
        val EXTRA_COCKTAIL_IDS = "extra_cocktail_ids"
        val EXTRA_USER_ID = "extra_user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // TODO bundle is always empty -> check why
        val pendingCocktailIds = intent.getStringArrayListExtra(EXTRA_COCKTAIL_IDS)
        val userId = intent.getStringExtra(EXTRA_USER_ID)

        if (pendingCocktailIds == null) {
            Log.e("TAG_CART", "pendingCocktails was null")
            finish()
            return
        }

        val currentOrder = MutableOrder(pendingCocktailIds, userId ?: "")
        val orderView = findViewById(R.id.cart_order_view) as OrderView
        orderView.mutableOrder = currentOrder

        findViewById(R.id.cart_send_order_button).setOnClickListener { sendOrder(currentOrder) }
    }

    private fun sendOrder(currentOrder: MutableOrder) {
        ApiManager.INSTANCE.createOrder(
                currentOrder,
                object : Callback<Void> {
                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        Log.i("TAG_ORDER_CREATE", t?.message)
                    }
                })
    }
}
