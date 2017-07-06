package de.adorsys.android.summerparty.ui

import android.graphics.drawable.Drawable
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
        private val cocktails: List<Cocktail>,
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
        private val addImage = view.findViewById(R.id.add_image) as ImageView

        private var item: Cocktail? = null

        fun bindItem(cocktail: Cocktail) {
            item = cocktail
            val id = cocktail.id.toInt()
            val cocktailDrawable = getCocktailDrawable(id)
            cocktailImageView.setImageDrawable(cocktailDrawable)

            contentView.text = cocktail.name
            if (cocktail.available) {
                availabilityView.setImageDrawable(view.context.getDrawable(R.drawable.ic_wb_sunny_green_24dp))
                availabilityText.text = availabilityText.context.getString(R.string.availability_available)
            } else {
                availabilityView.setImageDrawable(view.context.getDrawable(R.drawable.ic_wb_cloudy_red_24dp))
                availabilityText.text = availabilityText.context.getString(R.string.availability_unavailable)
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

        private fun getCocktailDrawable(id: Int): Drawable? {
            when (id) {
                CocktailType.ITALIAN_COLADA.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.pina_colada, cocktailImageView.context.theme)
                CocktailType.SEX_ON_THE_BEACH.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.sex_on_the_beach, cocktailImageView.context.theme)
                CocktailType.CAIPIRINHA.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.caipirinha, cocktailImageView.context.theme)
                CocktailType.MAI_TAI.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.mai_tai, cocktailImageView.context.theme)
                CocktailType.CHINATOWN.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.chinatown, cocktailImageView.context.theme)
                CocktailType.COCONUT_KISS.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.coconut_kiss, cocktailImageView.context.theme)
                CocktailType.SUNFLOWER.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.sunflower, cocktailImageView.context.theme)
                CocktailType.WODKA_LEMON.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.wodka_lemon, cocktailImageView.context.theme)
                CocktailType.CUBA_LIBRE.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.cuba_libre, cocktailImageView.context.theme)
                CocktailType.APEROL_SPRITZ.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.aperol_sprizz, cocktailImageView.context.theme)
                CocktailType.GIN_TONIC.id
                -> return cocktailImageView.resources.getDrawable(R.drawable.gin_tonic, cocktailImageView.context.theme)
            }
            return null
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
}
