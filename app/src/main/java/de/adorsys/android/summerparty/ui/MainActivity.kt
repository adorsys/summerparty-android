package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Toast
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.CocktailManager
import de.adorsys.android.summerparty.data.MutableCustomer
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), OrderFragment.OnListFragmentInteractionListener {
    private var viewPager: ViewPager? = null
    private val cocktails = ArrayList<Cocktail>()
    private val cocktailsCallback = CocktailsCallback()
    private val orderCallback = OrderCallback()
    private val cartActionCallback = CartActionCallback()
    private var currentOrder: MutableOrder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init views
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        viewPager = findViewById(R.id.container) as ViewPager
        val tabLayout = findViewById(R.id.tabs) as TabLayout

		setSupportActionBar(toolbar)
		// Create the adapter that will return a fragment for each of the two
		// primary sections of the activity.
		val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
		// Set up the ViewPager with the sections adapter.
		viewPager!!.adapter = sectionsPagerAdapter
		viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
		tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        CocktailManager.INSTANCE.getCocktails(cocktailsCallback)
    }

    override fun onListFragmentInteraction(item: Cocktail) {
        val viewContainer = findViewById(R.id.main_content)
        if (viewContainer != null && item.available) {
            Snackbar.make(viewContainer, getString(R.string.order_cocktail, item.name), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_cart, CartActionCallback())
                    .show()
        } else if (viewContainer != null) {
            Snackbar.make(viewContainer, getString(R.string.cocktail_out_of_stock, item.name), Snackbar.LENGTH_LONG)
                    .show()
        }
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val PAGER_COUNT: Int = 2
        private val COCKTAIL_COLUMN_COUNT: Int = 1

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


    // Callbacks
    private inner class CocktailsCallback : Callback<List<Cocktail>> {
        override fun onResponse(call: Call<List<Cocktail>>, response: Response<List<Cocktail>>) {
            cocktails.clear()
            val cocktailResponse: List<Cocktail>? = response.body()
            cocktailResponse?.let { cocktails.addAll(it) }
            viewPager?.adapter?.notifyDataSetChanged()
            Log.i("TAG", "success")
        }

        override fun onFailure(call: Call<List<Cocktail>>, t: Throwable) {
            Log.i("TAG", t.message)
        }
    }

    private inner class OrderCallback : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Toast.makeText(this@MainActivity, "Successfully created order", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.i("TAG", t.message)
        }
    }

    private inner class CartActionCallback : View.OnClickListener {
        override fun onClick(v: View) {
            // TODO add item to cart instead of directly creating order
            if (currentOrder != null) {
                CocktailManager.INSTANCE.createOrder(currentOrder!!, orderCallback)
            }
        }
    }
}
