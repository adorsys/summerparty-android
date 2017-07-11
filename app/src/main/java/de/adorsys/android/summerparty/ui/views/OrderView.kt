package de.adorsys.android.summerparty.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.CocktailUtils
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.ui.MainActivity

class OrderView : LinearLayout {
    var order: Order? = null
        get() = field
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

        val preferences = context.getSharedPreferences(MainActivity.KEY_PREFS_FILENAME, Context.MODE_PRIVATE)
        userNameTextView?.text = preferences.getString(MainActivity.KEY_USER_NAME, null)

        cocktailsContainer?.removeAllViews()
        val cocktailMap = CocktailUtils.cocktailListToMap(order.beverages.sortedWith(compareBy({ it.type })))
        for ((cocktail, count) in cocktailMap) {
            val cocktailTextView = LayoutInflater.from(context).inflate(R.layout.text_view, cocktailsContainer, false) as TextView
            val text = cocktailTextView.context.getString(R.string.order_cocktail_placeholder, count, cocktail.name)
            cocktailTextView.text = text
            cocktailsContainer?.addView(cocktailTextView)
        }

    }
}
