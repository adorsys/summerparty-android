package de.adorsys.android.summerparty.data.callback

import android.view.View
import android.widget.Toast

class CartActionCallback : View.OnClickListener {
    override fun onClick(v: View) {
        // TODO add item to cart
        Toast.makeText(v.context, "Added item to cart", Toast.LENGTH_LONG).show()
    }
}
