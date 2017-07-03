package de.adorsys.android.summerparty.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.CocktailType

class CocktailView : RelativeLayout {
    var cocktail: Cocktail? = null
        get() = field
        set(value) {
            field = value
            bindCocktail(value)
        }

    private var cocktailImageView: ImageView? = null
    private var contentView: TextView? = null
    private var availabilityView: ImageView? = null

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
        val view = LayoutInflater.from(context).inflate(R.layout.view_cocktail, this)

        cocktailImageView = view.findViewById(R.id.cocktail_imageView) as ImageView
        contentView = view.findViewById(R.id.order_view) as TextView
        availabilityView = view.findViewById(R.id.available_imageView) as ImageView
    }

    private fun bindCocktail(cocktail: Cocktail?) {
        val id = cocktail?.id?.toInt()
        cocktailImageView!!.setImageDrawable(
                if (id == CocktailType.MAI_TAI.id) {
                    cocktailImageView!!.resources.getDrawable(R.drawable.mai_tai, cocktailImageView!!.context.theme)
                } else if (id == CocktailType.CUBRA_LIBRE.id) {
                    cocktailImageView!!.resources.getDrawable(R.drawable.cuba_libre, cocktailImageView!!.context.theme)
                } else if (id == CocktailType.GIN_TONIC.id) {
                    cocktailImageView!!.resources.getDrawable(R.drawable.gin_tonic, cocktailImageView!!.context.theme)
                } else {
                    cocktailImageView!!.resources.getDrawable(R.drawable.moscow_mule, cocktailImageView!!.context.theme)
                })
        contentView!!.text = cocktail?.name
        availabilityView!!.setImageDrawable(
                if (cocktail != null && cocktail.available) {
                    context.getDrawable(R.drawable.ic_wb_sunny_green_24dp)
                } else {
                    context.getDrawable(R.drawable.ic_wb_cloudy_red_24dp)
                })
    }
}
