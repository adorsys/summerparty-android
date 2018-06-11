package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.ui.adapter.OrderRecyclerViewAdapter


class OrderFragment : Fragment() {
    private val orders = mutableListOf<Order>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        arguments?.let {
            orders.clear()
            orders.addAll(it.getParcelableArrayList(OrderFragment.ARG_ORDERS))
        }
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)

        // Set the adapter
        val context = view.context
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        val emptyListText = view.findViewById<TextView>(R.id.empty_list_text)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val sortedOrders = sortOrders(orders)
        recyclerView.adapter = OrderRecyclerViewAdapter(sortedOrders)
        recyclerView.setHasFixedSize(true)

        recyclerView.visibility = if (orders.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
        emptyListText.visibility = if (orders.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        return view
    }

    private fun sortOrders(orders: List<Order>): List<Order> {
        return orders.sortedWith(compareBy({ it.state })).asReversed()
    }

    companion object {
        private const val ARG_ORDERS = "orders"

        fun newInstance(orders: ArrayList<Order>): OrderFragment {
            val fragment = OrderFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_ORDERS, orders)
            fragment.arguments = args
            return fragment
        }
    }
}
