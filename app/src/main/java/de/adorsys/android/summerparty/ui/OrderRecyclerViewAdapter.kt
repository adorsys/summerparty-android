package de.adorsys.android.summerparty.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.ui.views.OrderView

class OrderRecyclerViewAdapter(
        private val orders: ArrayList<Order>) : RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItem(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val orderView: OrderView = view.findViewById(R.id.order_view) as OrderView

        fun bindItem(order: Order) {
            orderView.order = order
        }
    }
}
