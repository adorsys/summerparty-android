package de.adorsys.android.summerparty.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import de.adorsys.android.summerparty.ui.CocktailFragment
import de.adorsys.android.summerparty.ui.OrderFragment

class SectionsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        private const val PAGER_COUNT: Int = 2
    }

    private var cocktailFragment: CocktailFragment? = null
    private var orderFragment: OrderFragment? = null

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return if (position == 0) {
            if (cocktailFragment == null) {
                cocktailFragment = CocktailFragment.newInstance()
            }
            cocktailFragment!!
        } else {
            if (orderFragment == null) {
                orderFragment = OrderFragment.newInstance()
            }
            orderFragment!!
        }
    }

    override fun getItemPosition(any: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return PAGER_COUNT
    }
}