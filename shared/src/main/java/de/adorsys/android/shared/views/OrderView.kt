package de.adorsys.android.shared.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.adorsys.android.network.Order
import de.adorsys.android.shared.CocktailUtils
import de.adorsys.android.shared.R

class OrderView : LinearLayout {
    var order: Order? = null
        set(value) {
            field = value
            bindOrder(field)
        }

    private var statusImageView: ImageView? = null
    private var cocktailsContainer: LinearLayout? = null
    private var userNameTextView: TextView? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }


    private fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.view_order, this)
        statusImageView = view.findViewById(R.id.order_image) as ImageView
        cocktailsContainer = view.findViewById(R.id.order_cocktails_container) as LinearLayout
        userNameTextView = view.findViewById(R.id.order_user_name_text) as TextView
    }

    private fun bindOrder(order: Order?) {
        if (order == null) {
            return
        }

        val state = order.state
        when (state) {
            "mixed", "delivered" -> statusImageView?.setImageDrawable(
                    statusImageView!!.resources.getDrawable(R.drawable.cocktail_ready, statusImageView!!.context.theme))
            state -> statusImageView?.setImageDrawable(
                    statusImageView!!.resources.getDrawable(R.drawable.cocktail_ordered, statusImageView!!.context.theme))
        }

        userNameTextView?.text = order.customer.name

        cocktailsContainer?.removeAllViews()
        val cocktailMap = CocktailUtils.cocktailListToMap(order.beverages.sortedWith(compareBy { it.type }))
        for ((cocktail, count) in cocktailMap) {
            val cocktailTextView = LayoutInflater.from(context).inflate(R.layout.text_view, cocktailsContainer, false) as TextView
            val text = cocktailTextView.context.getString(R.string.order_cocktail_placeholder, count, cocktail.name)
            cocktailTextView.text = text
            cocktailsContainer?.addView(cocktailTextView)
        }

    }
}
