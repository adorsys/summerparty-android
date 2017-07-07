package de.adorsys.android.summerparty.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.ui.MainActivity

class CocktailView : LinearLayout {
    var cocktail: Cocktail? = null
        get() = field
        set(value) {
            field = value
            bindCocktail(value)
        }

    private var cocktailNameText: TextView? = null
    private var userNameText: TextView? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    private fun init() {
        orientation = VERTICAL
        val view = LayoutInflater.from(context).inflate(R.layout.view_cocktail, this)
        userNameText = view.findViewById(R.id.cocktail_user_name_text) as TextView
        cocktailNameText = view.findViewById(R.id.cocktail_name_text) as TextView
    }

    private fun bindCocktail(cocktail: Cocktail?) {
        val preferences = (context as MainActivity).getSharedPreferences(MainActivity.PREFS_FILENAME, Context.MODE_PRIVATE)
        userNameText!!.text = preferences.getString(MainActivity.KEY_USER_NAME, null)
        cocktailNameText!!.text = cocktail?.name
    }
}
