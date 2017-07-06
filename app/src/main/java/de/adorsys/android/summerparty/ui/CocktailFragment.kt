package de.adorsys.android.summerparty.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail

class CocktailFragment : Fragment() {
	private var columnCount = 2
	private var listener: CocktailFragment.OnListFragmentInteractionListener? = null
    private val cocktails: ArrayList<Cocktail> = ArrayList()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
        if (arguments != null) {
            columnCount = arguments.getInt(Companion.ARG_COLUMN_COUNT)
            cocktails.clear()
            cocktails.addAll(arguments.getParcelableArrayList<Cocktail>(Companion.ARG_COCKTAILS))
        }

		val view = inflater!!.inflate(R.layout.fragment_cocktail_list, container, false)

		// Set the adapter
		if (view is RecyclerView) {
			val context = view.getContext()
			val recyclerView = view
			if (columnCount <= 1) {
				recyclerView.layoutManager = LinearLayoutManager(context)
			} else {
				recyclerView.layoutManager = GridLayoutManager(context, columnCount)
			}
			val sortedCocktails = sortCocktails(cocktails)
			recyclerView.adapter = CocktailRecyclerViewAdapter(sortedCocktails, listener)
			recyclerView.setHasFixedSize(true)
		}
		return view
	}


	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is CocktailFragment.OnListFragmentInteractionListener) {
			listener = context
		} else {
			throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
		}
	}

	override fun onDetach() {
		super.onDetach()
		listener = null
	}

	private fun sortCocktails(cocktails: ArrayList<Cocktail>): List<Cocktail> {
        return cocktails.sortedWith(compareBy({ it.type }))
    }

	interface OnListFragmentInteractionListener {
		fun onListFragmentInteraction(item: Cocktail)
	}

	companion object {
		private val ARG_COLUMN_COUNT = "cocktails_column_count"
		private val ARG_COCKTAILS = "cocktails"

		fun newInstance(columnCount: Int, cocktails: ArrayList<Cocktail>): CocktailFragment {
			val fragment = CocktailFragment()
			val args = Bundle()
			args.putInt(CocktailFragment.Companion.ARG_COLUMN_COUNT, columnCount)
			args.putParcelableArrayList(CocktailFragment.Companion.ARG_COCKTAILS, cocktails)
			fragment.arguments = args
			return fragment
		}
	}
}
