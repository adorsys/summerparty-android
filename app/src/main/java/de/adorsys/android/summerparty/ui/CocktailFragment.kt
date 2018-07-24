package de.adorsys.android.summerparty.ui

import android.arch.lifecycle.Observer
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
import de.adorsys.android.summerparty.Repository
import de.adorsys.android.summerparty.ui.adapter.CocktailRecyclerViewAdapter

class CocktailFragment : Fragment() {
    private var listener: CocktailFragment.OnListFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cocktail_list, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = CocktailRecyclerViewAdapter(listener)
            view.setHasFixedSize(true)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Repository.cocktailsLiveData.observe(this, Observer { cocktailList ->
            (view as? RecyclerView).let { recyclerView ->
                val adapter = (recyclerView?.adapter as? CocktailRecyclerViewAdapter)
                adapter?.submitList(sortCocktails(cocktailList.orEmpty()))
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Repository.fetchCocktails()
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

    private fun sortCocktails(cocktails: List<Cocktail>): List<Cocktail> {
        return cocktails.sortedWith(compareBy { it.type })
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Cocktail)
    }

    companion object {
        fun newInstance(): CocktailFragment {
            return CocktailFragment()
        }
    }
}
