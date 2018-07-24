package de.adorsys.android.summerparty.ui.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.adorsys.android.network.Order
import de.adorsys.android.shared.views.OrderView
import de.adorsys.android.summerparty.R

class OrderRecyclerViewAdapter : ListAdapter<Order, OrderRecyclerViewAdapter.OrderViewHolder>(DiffUtilItemCallback()) {
    private val orders = mutableListOf<Order>()

    override fun submitList(list: List<Order>?) {
        orders.clear()
        orders.addAll(list.orEmpty())
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }


    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val orderView = view.findViewById(R.id.order_view) as OrderView
        private val headerView = view.findViewById(R.id.cocktail_state_header) as TextView

        fun bindItem(order: Order) {
            orderView.order = order

            var headerText: String? = ""
            when (order.state) {
                "mixed" -> headerText = headerView.context.getString(R.string.order_state_mixed)
                "delivered" -> headerText = headerView.context.getString(R.string.order_state_delivered)
                "ordered" -> headerText = headerView.context.getString(R.string.order_state_ordered)
            }

            if (adapterPosition == 0 || order.state != orders[adapterPosition - 1].state) {
                headerView.visibility = View.VISIBLE
                headerView.text = headerText
            } else {
                headerView.visibility = View.GONE
            }
        }
    }

    class DiffUtilItemCallback: DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
