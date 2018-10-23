package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.adorsys.android.network.Order
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.Repository
import de.adorsys.android.summerparty.ui.adapter.OrderRecyclerViewAdapter


class OrderFragment : Fragment() {
    private lateinit var emptyListText: TextView
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)

        emptyListText = view.findViewById(R.id.empty_list_text)

        // Set the adapter
        recyclerView = view.findViewById(R.id.list)

        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = OrderRecyclerViewAdapter()
        recyclerView.setHasFixedSize(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Repository.ordersLiveData.observe(this, Observer { orders ->
            setViewState(orders.orEmpty())
            val adapter = (recyclerView.adapter as? OrderRecyclerViewAdapter)
            adapter?.submitList(sortOrders(orders.orEmpty()))
        })
    }

    override fun onResume() {
        super.onResume()
        Repository.fetchOrders(forceReload = true)
    }

    private fun sortOrders(orders: List<Order>): List<Order> {
        return orders.sortedWith(compareBy { it.state }).asReversed()
    }

    private fun setViewState(orders: List<Order>) {
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
    }

    companion object {
        fun newInstance(): OrderFragment {
            return OrderFragment()
        }
    }
}
