package de.adorsys.android.summerparty.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.CocktailType

class CocktailRecyclerViewAdapter(
        private val cocktails: ArrayList<Cocktail>,
        private val listener: CocktailFragment.OnListFragmentInteractionListener?) : RecyclerView.Adapter<CocktailRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cocktail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holderOrder: CocktailRecyclerViewAdapter.ViewHolder, position: Int) {
        holderOrder.bindItem(cocktails[position])
    }

    override fun getItemCount(): Int {
        return cocktails.size
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val headerView = view.findViewById(R.id.cocktail_type_header) as TextView
        private val cocktailImageView = view.findViewById(R.id.cocktail_image) as ImageView
        private val contentView = view.findViewById(R.id.name_text) as TextView
        private val availabilityView = view.findViewById(R.id.available_image) as ImageView
        private val availabilityText = view.findViewById(R.id.available_text) as TextView

        private var item: Cocktail? = null

        fun bindItem(cocktail: Cocktail) {
            item = cocktail
            val id = cocktail.id.toInt()
            cocktailImageView.setImageDrawable(
                    if (id == CocktailType.MAI_TAI.id) {
                        cocktailImageView.resources.getDrawable(R.drawable.mai_tai, cocktailImageView.context.theme)
                    } else if (id == CocktailType.CUBRA_LIBRE.id) {
                        cocktailImageView.resources.getDrawable(R.drawable.cuba_libre, cocktailImageView.context.theme)
                    } else if (id == CocktailType.GIN_TONIC.id) {
                        cocktailImageView.resources.getDrawable(R.drawable.gin_tonic, cocktailImageView.context.theme)
                    } else {
                        cocktailImageView.resources.getDrawable(R.drawable.moscow_mule, cocktailImageView.context.theme)
                    })
            contentView.text = cocktail.name
            if (cocktail.available) {
                availabilityView.setImageDrawable(view.context.getDrawable(R.drawable.ic_wb_sunny_green_24dp))
                availabilityText.text = availabilityText.context.getString(R.string.availability_available)
            } else {
                availabilityView.setImageDrawable(view.context.getDrawable(R.drawable.ic_wb_cloudy_red_24dp))
                availabilityText.text = availabilityText.context.getString(R.string.availability_unavailable)
            }

            view.setOnClickListener {
                listener?.onListFragmentInteraction(cocktail)
            }
            if (adapterPosition == 0) {
                headerView.visibility = View.VISIBLE
                headerView.text = headerView.context.getString(R.string.cocktail_type_cocktail)
            } else {
                headerView.visibility = View.GONE
            }
        }
    }
}
