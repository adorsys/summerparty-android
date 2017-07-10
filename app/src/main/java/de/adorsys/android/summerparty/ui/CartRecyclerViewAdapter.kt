package de.adorsys.android.summerparty.ui

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.CocktailUtil

class CartRecyclerViewAdapter(
        private val cocktails: MutableList<Cocktail>, private val cocktailMap: HashMap<Cocktail, Int>) : RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder>() {
    init {
        for (cocktail in cocktails) {
            if (cocktailMap.containsKey(cocktail)) {
                var idCount = cocktailMap[cocktail]
                idCount = idCount?.plus(1)
                idCount?.let { cocktailMap.replace(cocktail, it) }
            } else {
                cocktailMap.put(cocktail, 1)
            }
        }

        // build again cocktail list from cocktailMap as cocktailMap is sorted
        cocktails.clear()
        for ((cocktail) in cocktailMap) {
            cocktails.add(cocktail)
        }
    }

    fun getCocktailIds(): MutableList<String> {
        val cocktailIdList = ArrayList<String>()
        for ((key, count) in cocktailMap) {
            var counter = 0
            while (counter < count) {
                cocktailIdList.add(key.id)
                counter += 1
            }
        }
        return cocktailIdList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holderOrder: CartRecyclerViewAdapter.ViewHolder, position: Int) {
        holderOrder.bindItem(cocktails[position])
    }

    override fun getItemCount(): Int {
        return cocktails.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cocktailImage = view.findViewById(R.id.cocktail_image) as ImageView
        private val cocktailName = view.findViewById(R.id.name_text) as TextView
        private val cocktailCount = view.findViewById(R.id.cocktail_count) as EditText

        private var item: Cocktail? = null

        fun bindItem(cocktail: Cocktail) {
            item = cocktail
            cocktailCount.setText(cocktailMap[cocktail]?.toString())
            cocktailCount.setOnClickListener({
                cocktailCount.selectAll()
            })
            cocktailCount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val cocktailCount = s?.toString()?.toInt()
                    if (cocktailCount != null)
                    cocktailMap.replace(cocktail, cocktailCount)
                }
            })
            val cocktailDrawable = CocktailUtil.getCocktailDrawableForId(cocktailImage.context, cocktail.id)
            cocktailImage.setImageDrawable(cocktailDrawable)
            cocktailName.text = cocktail.name
        }
    }
}