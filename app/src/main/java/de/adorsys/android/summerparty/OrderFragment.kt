package de.adorsys.android.summerparty

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.summerparty.mock.CocktailMockContent

class OrderFragment : Fragment() {
	private var columnCount = 2
	private var listener: OnListFragmentInteractionListener? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if (arguments != null) {
			columnCount = arguments.getInt(ARG_COLUMN_COUNT)
		}
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
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
			recyclerView.adapter = CocktailRecyclerViewAdapter(CocktailMockContent.ITEMS, listener)
		}
		return view
	}


	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is OnListFragmentInteractionListener) {
			listener = context as OnListFragmentInteractionListener?
		} else {
			throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
		}
	}

	override fun onDetach() {
		super.onDetach()
		listener = null
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 *
	 *
	 * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
	 */
	interface OnListFragmentInteractionListener {
		// TODO: Update argument type and name
		fun onListFragmentInteraction(item: CocktailItem)
	}

	companion object {
		// TODO: Customize parameter argument names
		private val ARG_COLUMN_COUNT = "column-count"

		// TODO: Customize parameter initialization
		fun newInstance(columnCount: Int): OrderFragment {
			val fragment = OrderFragment()
			val args = Bundle()
			args.putInt(ARG_COLUMN_COUNT, columnCount)
			fragment.arguments = args
			return fragment
		}
	}
}
