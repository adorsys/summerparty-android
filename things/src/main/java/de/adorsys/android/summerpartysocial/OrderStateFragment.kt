package de.adorsys.android.summerpartysocial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.adorsys.android.network.ApiManager
import de.adorsys.android.network.Order
import de.adorsys.android.shared.Values
import de.adorsys.android.shared.views.OrderView
import kotlinx.android.synthetic.main.fragment_order_state.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class OrderStateFragment : Fragment() {
    val orders: MutableList<Order?> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_state, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOrders()
        pollOrders(10000)

        val adapter = OrderAdapter()
        order_recycler_view.adapter = adapter
        order_recycler_view.layoutManager = LinearLayoutManager(context)
    }

    private fun getOrders() {
        launch {
            try {
                val orderedResponse = ApiManager.getOrders("ordered").await()
                val mixedResponse = ApiManager.getOrders("mixed").await()
                if (orderedResponse.isSuccessful && mixedResponse.isSuccessful) {
                    val orderedList: List<Order> = orderedResponse.body().orEmpty()
                    val mixedList: List<Order> = mixedResponse.body().orEmpty()
                    // Update adapter's order list
                    val orderList = mutableListOf<Order>()
                    orderList.addAll(mixedList)
                    orderList.addAll(orderedList)
                    if (orders == orderList) return@launch
                    orders.clear()
                    orders.addAll(orderList)
                    launch(UI) { updateAdapter(orderList) }
                } else {
                    Log.i("TAG_CUSTOMER_ORDERS", "${orderedResponse.message()} ${mixedResponse.message()}")
                }
            } catch (e: Exception) {
                Log.i("TAG_CUSTOMER_ORDERS", e.message)
            }
        }
    }

    private fun pollOrders(delay: Long) {
        view?.postDelayed({
            getOrders()
            pollOrders(delay)
        }, delay)
    }


    private fun updateAdapter(list: List<Order?>) {
        (order_recycler_view.adapter as OrderAdapter).submitList(list)
    }


    inner class OrderAdapter : ListAdapter<Order, OrderAdapter.OrderViewHolder>(ListItemDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val orderView = view.findViewById(R.id.order_view) as OrderView
            private val headerView = view.findViewById(R.id.cocktail_state_header) as TextView

            fun bind(order: Order) {
                orderView.order = order

                var headerText: String? = ""
                when (order.state) {
                    "mixed" -> headerText = headerView.context.getString(R.string.order_state_mixed)
                    "delivered" -> headerText = headerView.context.getString(R.string.order_state_delivered)
                    "ordered" -> headerText = headerView.context.getString(R.string.order_state_ordered)
                }

                if (adapterPosition == 0 || order.state != orders[adapterPosition - 1]?.state) {
                    headerView.visibility = View.VISIBLE
                    headerView.text = headerText
                } else {
                    headerView.visibility = View.GONE
                }
            }
        }
    }

    class ListItemDiffCallback : DiffUtil.ItemCallback<Order?>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}