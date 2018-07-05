package de.adorsys.android.summerparty.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.network.Cocktail
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.ui.adapter.CocktailRecyclerViewAdapter

class CocktailFragment : Fragment() {
    private var listener: CocktailFragment.OnListFragmentInteractionListener? = null
    private val cocktails: ArrayList<Cocktail> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        arguments?.let {
            cocktails.clear()
            cocktails.addAll(it.getParcelableArrayList(ARG_COCKTAILS))
        }

        val view = inflater.inflate(R.layout.fragment_cocktail_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            val sortedCocktails = sortCocktails(cocktails)
            view.adapter = CocktailRecyclerViewAdapter(sortedCocktails, listener)
            view.setHasFixedSize(true)
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
        return cocktails.sortedWith(compareBy { it.type })
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Cocktail)
    }

    companion object {
        private const val ARG_COCKTAILS = "cocktails"

        fun newInstance(cocktails: ArrayList<Cocktail>): CocktailFragment {
            val fragment = CocktailFragment()
            val args = Bundle()
            args.putParcelableArrayList(CocktailFragment.ARG_COCKTAILS, cocktails)
            fragment.arguments = args
            return fragment
        }
    }
}
