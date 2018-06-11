package de.adorsys.android.summerparty.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.ui.CocktailFragment
import de.adorsys.android.summerparty.ui.OrderFragment

class SectionsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        private const val PAGER_COUNT: Int = 2
    }

    val cocktails = ArrayList<Cocktail>()
    val orders = ArrayList<Order>()

    fun setCocktails(cocktails: List<Cocktail>) {
        this.cocktails.clear()
        this.cocktails.addAll(cocktails)
    }

    fun setOrders(orders: List<Order>) {
        this.orders.clear()
        this.orders.addAll(orders)
    }

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return if (position == 0)
            CocktailFragment.newInstance(cocktails)
        else
            OrderFragment.newInstance(orders)
    }

    override fun getItemPosition(any: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return PAGER_COUNT
    }
}