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

    override fun onBindViewHolder(holder: CocktailRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItem(cocktails[position])
    }

    override fun getItemCount(): Int {
        return cocktails.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val cocktailImageView: ImageView = view.findViewById(R.id.cocktail_image) as ImageView
        private val contentView: TextView = view.findViewById(R.id.cocktail_name_text) as TextView
        private val availabilityView: ImageView = view.findViewById(R.id.cocktail_available_image) as ImageView
        var item: Cocktail? = null

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
            availabilityView.setImageDrawable(
                    if (cocktail.available) {
                        view.context.getDrawable(R.drawable.ic_wb_sunny_green_24dp)
                    } else {
                        view.context.getDrawable(R.drawable.ic_wb_cloudy_red_24dp)
                    })

            view.setOnClickListener {
                listener?.onListFragmentInteraction(cocktail)
            }
        }
    }
}
