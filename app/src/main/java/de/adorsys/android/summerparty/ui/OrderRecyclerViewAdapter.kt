package de.adorsys.android.summerparty.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val orderView: OrderView = view.findViewById(R.id.order_view) as OrderView
        val statusView: ImageView = view.findViewById(R.id.status_imageView) as ImageView

        fun bindItem(order: Order) {
            val state = order.state
            statusView.setImageDrawable(
                    if (state == "mixed") {
                        statusView.resources.getDrawable(R.drawable.traffic_lights_green, statusView.context.theme)
                    } else if (state == "delivered") {
                        statusView.resources.getDrawable(R.drawable.traffic_lights_orange, statusView.context.theme)
                    } else {
                        statusView.resources.getDrawable(R.drawable.traffic_light_red, statusView.context.theme)
                    })
            orderView.cocktails = order.beverages
        }
    }
}
