package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import de.adorsys.android.network.mutable.MutableOrder
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.Repository
import de.adorsys.android.summerparty.ui.adapter.CartRecyclerViewAdapter

class CartActivity : BaseActivity() {
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val pendingCocktails = Repository.pendingCocktailList

        recyclerView = findViewById(R.id.cart_order_items_recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = CartRecyclerViewAdapter(pendingCocktails.toMutableList(), object : CartRecyclerViewAdapter.OnListEmptyListener {
            override fun onListEmpty() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
        recyclerView?.setHasFixedSize(true)

        findViewById<View>(R.id.cart_send_order_button).setOnClickListener {
            val currentOrder = MutableOrder((recyclerView?.adapter as CartRecyclerViewAdapter).getCocktailIds(), Repository.user?.id ?: "")
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
        Repository.createOrder(currentOrder) {
            if (it) {
                setResult(Activity.RESULT_OK)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }
}
