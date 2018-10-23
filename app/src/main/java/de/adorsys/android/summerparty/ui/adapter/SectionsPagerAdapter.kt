package de.adorsys.android.summerparty.ui.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.adorsys.android.summerparty.ui.CocktailFragment
import de.adorsys.android.summerparty.ui.OrderFragment

class SectionsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        private const val PAGER_COUNT: Int = 2
    }

    private var cocktailFragment: CocktailFragment? = null
    private var orderFragment: OrderFragment? = null

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
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
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return PAGER_COUNT
    }
}