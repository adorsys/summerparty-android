package de.adorsys.android.summerparty.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
		holder.item = cocktails[position]
		holder.idView.text = cocktails[position].id
		holder.contentView.text = cocktails[position].type?.name

		holder.view.setOnClickListener {
			listener?.onListFragmentInteraction(holder.item as Cocktail)
		}
	}

	override fun getItemCount(): Int {
		return cocktails.size
	}

	inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
		val idView: TextView
		val contentView: TextView
		var item: Cocktail? = null

		init {
			idView = view.findViewById(R.id.id) as TextView
			contentView = view.findViewById(R.id.content) as TextView
		}

		override fun toString(): String {
			return super.toString() + " '" + contentView.text + "'"
		}
	}
}
