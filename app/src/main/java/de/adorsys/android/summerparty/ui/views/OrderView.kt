package de.adorsys.android.summerparty.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Order

class OrderView : LinearLayout {
    var order: Order? = null
        get() = field
        set(value) {
            field = value
            bindOrder(field)
        }

    private var statusImageView: ImageView? = null
    private var cocktailsContainer: LinearLayout? = null

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
        statusImageView = view.findViewById(R.id.order_status_image) as ImageView
        cocktailsContainer = view.findViewById(R.id.cocktails_container) as LinearLayout
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

        cocktailsContainer?.removeAllViews()
        for (cocktail in order.beverages) {
            val cocktailView = CocktailView(context)
            cocktailView.cocktail = cocktail
            cocktailsContainer?.addView(cocktailView)
        }
    }
}
