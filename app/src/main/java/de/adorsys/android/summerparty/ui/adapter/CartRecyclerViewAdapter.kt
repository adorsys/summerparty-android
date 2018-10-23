package de.adorsys.android.summerparty.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.adorsys.android.network.Cocktail
import de.adorsys.android.shared.CocktailUtils
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.Repository

class CartRecyclerViewAdapter(
        private val cocktails: MutableList<Cocktail>,
        private val onListEmptyListener: OnListEmptyListener,
        private var cocktailMap: MutableMap<Cocktail, Int> = mutableMapOf()) : RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder>() {
    init {
        cocktailMap = CocktailUtils.cocktailListToMap(cocktails)

        // build again cocktail list from cocktailMap as cocktailMap is sorted and doesn't contain duplicates
        cocktails.clear()
        for ((cocktail) in cocktailMap) {
            cocktails.add(cocktail)
        }
    }

    fun getCocktailIds(): List<String> {
        return CocktailUtils.cocktailMapToIdList(cocktailMap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holderOrder: ViewHolder, position: Int) {
        holderOrder.bindItem(cocktails[position])
    }

    override fun getItemCount(): Int {
        return cocktails.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cocktailImage = view.findViewById(R.id.cocktail_image) as ImageView
        private val cocktailName = view.findViewById(R.id.name_text) as TextView
        private val cocktailCountText = view.findViewById(R.id.cocktail_count) as EditText
        private val cocktailDelete = view.findViewById(R.id.cocktail_delete) as ImageButton

        private var item: Cocktail? = null

        fun bindItem(cocktail: Cocktail) {
            item = cocktail
            cocktailCountText.setText(cocktailMap[cocktail]?.toString())
            cocktailCountText.setOnClickListener {
                cocktailCountText.selectAll()
            }
            cocktailCountText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val cocktailCount = s?.toString()
                    if (!cocktailCount.isNullOrBlank()) {
                        cocktailCountText.selectAll()
                        cocktailMap.remove(cocktail)
                        cocktailMap[cocktail] = cocktailCount!!.toInt()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            cocktailDelete.setOnClickListener {
                cocktails.remove(cocktail)
                cocktailMap.remove(cocktail)
                Repository.removeFromPendingCocktails(cocktail)
                notifyDataSetChanged()
                if (cocktails.isEmpty()) {
                    onListEmptyListener.onListEmpty()
                }
            }
            val cocktailDrawable = CocktailUtils.getCocktailDrawableForName(cocktailImage.context, cocktail.name)
            cocktailImage.setImageDrawable(cocktailDrawable)
            cocktailName.text = cocktail.name
        }
    }

    interface OnListEmptyListener {
        fun onListEmpty()
    }
}