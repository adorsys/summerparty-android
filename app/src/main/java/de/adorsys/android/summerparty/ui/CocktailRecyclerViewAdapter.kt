package de.adorsys.android.summerparty.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail

class CocktailRecyclerViewAdapter(
        private val cocktails: ArrayList<Cocktail>,
        private val listener: OrderFragment.OnListFragmentInteractionListener?) : RecyclerView.Adapter<CocktailRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CocktailRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItem(cocktails[position])
    }

    override fun getItemCount(): Int {
        return cocktails.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.id_textView) as TextView
        val contentView: TextView = view.findViewById(R.id.name_textView) as TextView
        val availabilityView: ImageView = view.findViewById(R.id.available_imageView) as ImageView
        var item: Cocktail? = null

        fun bindItem(cocktail: Cocktail) {
            item = cocktail
            idView.text = cocktail.id
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
