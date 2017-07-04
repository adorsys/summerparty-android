package de.adorsys.android.summerparty.ui

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Order

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
        val contentView: TextView = view.findViewById(R.id.name_textView) as TextView
        val statusView: ImageView = view.findViewById(R.id.status_imageView) as ImageView
        var item: Order? = null

        fun bindItem(order: Order) {
            item = order
            val state = order.state
            statusView.setImageDrawable(
                    if (state == "mixed") {
                        statusView.resources.getDrawable(R.drawable.traffic_lights_green, statusView.context.theme)
                    } else if (state == "delivered") {
                        statusView.resources.getDrawable(R.drawable.traffic_lights_orange, statusView.context.theme)
                    } else {
                        statusView.resources.getDrawable(R.drawable.traffic_light_red, statusView.context.theme)
                    })
            // TODO change to nicer ui
            contentView.text = buildOrderString(order)
        }

        private fun buildOrderString(order: Order): CharSequence? {
            var orderString = ""
            for (cocktail in order.beverages) {
                orderString = orderString.plus(cocktail.name).plus("\n")
            }
            if (!TextUtils.isEmpty(orderString)) {
                return orderString.subSequence(0, orderString.length - 1)
            } else {
                return ""
            }
        }
    }
}
