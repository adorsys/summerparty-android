package de.adorsys.android.summerparty.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.Order

class OrderView : LinearLayout {
    var statusImageView: ImageView? = null
    var cocktails: List<Cocktail>? = null
        get() = field
        set(value) {
            field = value
            removeAllViews()
            value?.map { createCocktailView(it) }?.forEach { addView(it) }
        }
    var order: Order? = null
        get() = field
        set(value) {
            field = value
            bindOrder(field)
        }

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
        statusImageView = view.findViewById(R.id.status_imageView) as ImageView
    }

    private fun bindOrder(order: Order?) {
        val state = order?.state
        statusImageView?.setImageDrawable(
                if (state == "mixed") {
                    statusImageView!!.resources.getDrawable(R.drawable.traffic_lights_green, statusImageView!!.context.theme)
                } else if (state == "delivered") {
                    statusImageView!!.resources.getDrawable(R.drawable.traffic_lights_orange, statusImageView!!.context.theme)
                } else {
                    statusImageView!!.resources.getDrawable(R.drawable.traffic_light_red, statusImageView!!.context.theme)
                })
    }

    private fun createCocktailView(cocktail: Cocktail): View {
        val view = CocktailView(this.context)
        view.cocktail = cocktail
        return view
    }
}
