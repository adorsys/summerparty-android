package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Android
import de.adorsys.android.summerparty.data.ApiManager
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import de.adorsys.android.summerparty.ui.adapter.CartRecyclerViewAdapter
import kotlinx.coroutines.experimental.launch

class CartActivity : BaseActivity() {
    companion object {
        val EXTRA_COCKTAILS = "extra_cocktail_ids"
        val EXTRA_USER_ID = "extra_user_id"
    }

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // if activity is left result should be canceled
        setResult(Activity.RESULT_CANCELED)

        val pendingCocktails = intent.getParcelableArrayListExtra<Cocktail>(EXTRA_COCKTAILS)
        val userId = intent.getStringExtra(EXTRA_USER_ID)

        if (pendingCocktails == null) {
            Log.e("TAG_CART", "pendingCocktails was null")
            finish()
            return
        }

        recyclerView = findViewById(R.id.cart_order_items_recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        val sortedCocktails: List<Cocktail> = pendingCocktails.sortedWith(compareBy({ it.id }))
        recyclerView?.adapter = CartRecyclerViewAdapter(sortedCocktails.toMutableList(), object : CartRecyclerViewAdapter.OnListEmptyListener {
            override fun onListEmpty() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
        recyclerView?.setHasFixedSize(true)

        findViewById<View>(R.id.cart_send_order_button).setOnClickListener {
            val currentOrder = MutableOrder((recyclerView?.adapter as CartRecyclerViewAdapter).getCocktailIds(), userId ?: "")
            sendOrder(currentOrder)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        return true
    }

    private fun sendOrder(currentOrder: MutableOrder) {
        launch(Android) {
            val response = ApiManager.createOrder(currentOrder).await()
            try {
                if (response.isSuccessful) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            } catch (e: Exception) {
                Log.i("TAG_ORDER_CREATE", e.message)
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}
