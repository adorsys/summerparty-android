package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Order


class StatusFragment : Fragment() {
	private var columnCount = 2
	private val orders: ArrayList<Order> = ArrayList()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		if (arguments != null) {
			columnCount = arguments.getInt(StatusFragment.ARG_COLUMN_COUNT)
			orders.clear()
			orders.addAll(arguments.getParcelableArrayList<Order>(StatusFragment.ARG_ORDERS))
		}

		// Inflate the layout for this fragment
		val view = inflater!!.inflate(R.layout.fragment_status, container, false)

		// Set the adapter
		if (view is RecyclerView) {
			val context = view.getContext()
			val recyclerView = view
			if (columnCount <= 1) {
				recyclerView.layoutManager = LinearLayoutManager(context)
			} else {
				recyclerView.layoutManager = GridLayoutManager(context, columnCount)
			}
			recyclerView.adapter = OrderRecyclerViewAdapter(orders)
		}
		return view
	}

	companion object {
		private val ARG_COLUMN_COUNT = "status_column_count"
		private val ARG_ORDERS = "orders"

		fun newInstance(columnCount: Int, orders: ArrayList<Order>): StatusFragment {
			val fragment = StatusFragment()
			val args = Bundle()
			args.putInt(ARG_COLUMN_COUNT, columnCount)
			args.putParcelableArrayList(ARG_ORDERS, orders)
			fragment.arguments = args
			return fragment
		}
	}
}
