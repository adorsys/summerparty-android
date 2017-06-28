package de.adorsys.android.summerparty.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import de.adorsys.android.summerparty.data.Cocktail

class SectionsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val PAGER_COUNT: Int = 2
    private val COCKTAIL_COLUMN_COUNT: Int = 1

    private val cocktails = ArrayList<Cocktail>()

    fun setCocktails(cocktails: List<Cocktail>) {
        this.cocktails.clear()
        this.cocktails.addAll(cocktails)
    }

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return if (position == 0)
            OrderFragment.Companion.newInstance(COCKTAIL_COLUMN_COUNT, cocktails)
        else
            StatusFragment.Companion.newInstance()
    }

    override fun getItemPosition(`object`: Any?): Int {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return PAGER_COUNT
    }
}