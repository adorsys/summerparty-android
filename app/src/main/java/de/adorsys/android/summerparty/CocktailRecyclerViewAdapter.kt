package de.adorsys.android.summerparty

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.adorsys.android.summerparty.OrderFragment.OnListFragmentInteractionListener

class CocktailRecyclerViewAdapter(private val values: List<CocktailItem>, private val listener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<CocktailRecyclerViewAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
				.inflate(R.layout.fragment_order, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.item = values[position]
		holder.idView.text = values[position].id
		holder.contentView.text = values[position].description

		holder.view.setOnClickListener {
			listener?.onListFragmentInteraction(holder.item as CocktailItem)
		}
	}

	override fun getItemCount(): Int {
		return values.size
	}

	inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
		val idView: TextView
		val contentView: TextView
		var item: CocktailItem? = null

		init {
			idView = view.findViewById(R.id.id) as TextView
			contentView = view.findViewById(R.id.content) as TextView
		}

		override fun toString(): String {
			return super.toString() + " '" + contentView.text + "'"
		}
	}
}
