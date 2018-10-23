package de.adorsys.android.summerparty.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.adorsys.android.network.Cocktail
import de.adorsys.android.shared.CocktailUtils
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.ui.CocktailFragment

class CocktailRecyclerViewAdapter(
        private val listener: CocktailFragment.OnListFragmentInteractionListener?) : ListAdapter<Cocktail, CocktailRecyclerViewAdapter.CocktailViewHolder>(DiffUtilItemCallback()) {

    val cocktails = mutableListOf<Cocktail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cocktail, parent, false)
        return CocktailViewHolder(view)
    }

    override fun onBindViewHolder(holderOrder: CocktailViewHolder, position: Int) {
        holderOrder.bindItem(getItem(position))
    }

    override fun submitList(list: List<Cocktail>?) {
        this.cocktails.clear()
        this.cocktails.addAll(list.orEmpty())
        super.submitList(list)
    }

    inner class CocktailViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val headerView = view.findViewById(R.id.cocktail_type_header) as TextView
        private val cocktailImageView = view.findViewById(R.id.cocktail_image) as ImageView
        private val contentView = view.findViewById(R.id.name_text) as TextView
        private val availabilityView = view.findViewById(R.id.available_image) as ImageView
        private val availabilityText = view.findViewById(R.id.available_text) as TextView
        private val addImage = view.findViewById(R.id.add_image) as ImageView
        private val cocktailOftheyear = view.findViewById(R.id.bestsellerImg) as ImageView

        private var item: Cocktail? = null

        fun bindItem(cocktail: Cocktail) {
            item = cocktail
            val cocktailDrawable = CocktailUtils.getCocktailDrawableForName(cocktailImageView.context, cocktail.name)
            cocktailImageView.setImageDrawable(cocktailDrawable)

            contentView.text = cocktail.name
            if (cocktail.available) {
                availabilityView.setImageDrawable(view.context.getDrawable(R.drawable.dot_green))
                availabilityText.text = availabilityText.context.getString(R.string.availability_available)
            } else {
                availabilityView.setImageDrawable(view.context.getDrawable(R.drawable.dot_red))
                availabilityText.text = availabilityText.context.getString(R.string.availability_unavailable)
            }

            if (cocktail.name.equals(view.context.getString(R.string.cocktail_of_the_year))) {
                cocktailOftheyear.visibility = View.VISIBLE
            }

            addImage.setOnClickListener {
                listener?.onListFragmentInteraction(cocktail)
            }
            if (adapterPosition == 0 || cocktails[adapterPosition - 1].type != cocktail.type) {
                headerView.visibility = View.VISIBLE
                headerView.text = getHeaderText(cocktail.type)
            } else {
                headerView.visibility = View.GONE
            }
        }

        private fun getHeaderText(type: String?): String? {
            when (type) {
                "cocktail" -> return headerView.context.getString(R.string.cocktail_type_cocktail)
                "nonalcoholic" -> return headerView.context.getString(R.string.cocktail_type_nonalcoholic)
                "longDrink" -> return headerView.context.getString(R.string.cocktail_type_long_drink)
            }
            return headerView.context.getString(R.string.cocktail_type_cocktail)
        }
    }

    class DiffUtilItemCallback: DiffUtil.ItemCallback<Cocktail>() {
        override fun areItemsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
            return oldItem == newItem
        }
    }
}
