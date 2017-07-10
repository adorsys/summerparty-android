package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.ApiManager
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import de.adorsys.android.summerparty.ui.adapter.CartRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity() {
    companion object {
        val EXTRA_COCKTAILS = "extra_cocktail_ids"
        val EXTRA_USER_ID = "extra_user_id"
    }

    private var recyclerView: RecyclerView? = null
    private val pendingCocktailIds = emptyList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val pendingCocktails = intent.getParcelableArrayListExtra<Cocktail>(EXTRA_COCKTAILS)
        val userId = intent.getStringExtra(EXTRA_USER_ID)

        if (pendingCocktails == null) {
            Log.e("TAG_CART", "pendingCocktails was null")
            finish()
            return
        }

        recyclerView = findViewById(R.id.cart_order_items_recycler_view) as RecyclerView
        recyclerView?.layoutManager = LinearLayoutManager(this)
        val sortedCocktails: List<Cocktail> = pendingCocktails.sortedWith(compareBy({ it.id }))
        recyclerView?.adapter = CartRecyclerViewAdapter(sortedCocktails.toMutableList(), HashMap())
        recyclerView?.setHasFixedSize(true)

        findViewById(R.id.cart_send_order_button).setOnClickListener {
            val currentOrder = MutableOrder((recyclerView?.adapter as CartRecyclerViewAdapter).getCocktailIds(), userId ?: "")
            sendOrder(currentOrder) }
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
