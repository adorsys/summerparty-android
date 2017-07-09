package de.adorsys.android.summerparty.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.CocktailType
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import de.adorsys.android.summerparty.ui.CartActivity
import de.adorsys.android.summerparty.ui.MainActivity

class OrderView : LinearLayout {
    var order: Order? = null
        get() = field
        set(value) {
            field = value
            bindOrder(field)
        }

    var mutableOrder: MutableOrder? = null
        get() = field
        set(value) {
            field = value
            bindOrder(value)
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

        Log.i("TAG_ORDER", order.id)
        val state = order.state
        statusImageView?.setImageDrawable(
                if (state == "mixed") {
                    statusImageView!!.resources.getDrawable(R.drawable.cocktail_ready, statusImageView!!.context.theme)
                } else if (state == "delivered") {
                    statusImageView!!.resources.getDrawable(R.drawable.cocktail_ready, statusImageView!!.context.theme)
                } else {
                    statusImageView!!.resources.getDrawable(R.drawable.cocktail_ordered, statusImageView!!.context.theme)
                })

        // TODO dirty context cast
        val preferences = (context as MainActivity).getPreferences(Context.MODE_PRIVATE)
        userNameTextView?.text = preferences.getString(MainActivity.KEY_USER_NAME, null)

        cocktailsContainer?.removeAllViews()
        for (cocktail in order.beverages) {
            val cocktailTextView = LayoutInflater.from(context).inflate(R.layout.text_view, cocktailsContainer, false) as TextView
            cocktailTextView.text = cocktail.name
            cocktailsContainer?.addView(cocktailTextView)
        }
    }

    private fun bindOrder(order: MutableOrder?) {
        if (order == null) {
            return
        }

        statusImageView?.visibility = View.GONE
        // TODO dirty context cast
        val preferences = (context as CartActivity).getPreferences(Context.MODE_PRIVATE)
        userNameTextView?.text = preferences.getString(MainActivity.KEY_USER_NAME, null)

        cocktailsContainer?.removeAllViews()
        for (cocktail in order.beverages) {
            val cocktailTextView = LayoutInflater.from(context).inflate(R.layout.text_view, cocktailsContainer, false) as TextView
            cocktailTextView.text = CocktailType.cocktailForId(cocktail.toInt())?.name
            cocktailsContainer?.addView(cocktailTextView)
        }
    }
}
