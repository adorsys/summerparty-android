package de.adorsys.android.summerparty.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.ApiManager.getCocktails
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.ui.adapter.SectionsPagerAdapter


class CocktailMainFragment : Fragment() {
    private var pendingCocktails = mutableListOf<Cocktail>()

    interface OnGetUserCocktailsListener {
        fun onGetUserCocktails()
    }

    private lateinit var viewPager: ViewPager
    private lateinit var listener: OnGetUserCocktailsListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.listener = context as OnGetUserCocktailsListener
        } catch (e: ClassCastException) {
            IllegalStateException("Your activity has to implement OnGetUserCocktailsListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cocktail_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        viewPager = view.findViewById(R.id.view_pager)


        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        val sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        // Set up the ViewPager with the sections adapter.

        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                getCocktails()
                listener.onGetUserCocktails()
            }
        })

        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
        tabLayout.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        getCocktails()
                        listener.onGetUserCocktails()
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabSelected(tab: TabLayout.Tab?) {}
                })
    }

    fun notifyDataSetChanged(orderList: List<Order> = emptyList(), cocktailList: List<Cocktail> = emptyList(), goToOrders: Boolean = false) {
        val oldCocktailList = (viewPager.adapter as SectionsPagerAdapter).cocktails
        if (oldCocktailList != cocktailList && cocktailList.isNotEmpty()) {
            (viewPager.adapter as SectionsPagerAdapter).setCocktails(cocktailList)
            viewPager.adapter?.notifyDataSetChanged()
        }

        val oldOrderList = (viewPager.adapter as SectionsPagerAdapter).orders
        if (oldOrderList != orderList && orderList.isNotEmpty()) {
            (viewPager.adapter as SectionsPagerAdapter).setOrders(orderList)
            viewPager.adapter?.notifyDataSetChanged()

            if (goToOrders) {
                viewPager.currentItem = 1
            }
        }
    }

    fun clearCocktailList() {
        pendingCocktails.clear()
    }

    fun addToCocktailList(item: Cocktail) {
        pendingCocktails.add(item)
    }

    fun getCocktailList(): ArrayList<Cocktail> {
        return ArrayList(pendingCocktails)
    }

}
