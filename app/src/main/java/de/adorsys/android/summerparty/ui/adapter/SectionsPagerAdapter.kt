package de.adorsys.android.summerparty.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.ui.CocktailFragment
import de.adorsys.android.summerparty.ui.StatusFragment

class SectionsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val PAGER_COUNT: Int = 2
    private val COCKTAIL_COLUMN_COUNT: Int = 1
    private val ORDER_COLUMN_COUNT: Int = 1

    private val cocktails = ArrayList<Cocktail>()
    private val orders = ArrayList<Order>()

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
            CocktailFragment.newInstance(COCKTAIL_COLUMN_COUNT, cocktails)
        else
            StatusFragment.newInstance(ORDER_COLUMN_COUNT, orders)
    }

    override fun getItemPosition(any: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return PAGER_COUNT
    }
}