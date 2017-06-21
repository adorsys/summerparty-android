package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Toast
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.CocktailItem

class MainActivity : AppCompatActivity(), OrderFragment.OnListFragmentInteractionListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// init views
		val toolbar = findViewById(R.id.toolbar) as Toolbar
		val viewPager = findViewById(R.id.container) as ViewPager
		val tabLayout = findViewById(R.id.tabs) as TabLayout

		setSupportActionBar(toolbar)
		// Create the adapter that will return a fragment for each of the two
		// primary sections of the activity.
		val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
		// Set up the ViewPager with the sections adapter.
		viewPager.adapter = sectionsPagerAdapter
		viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
		tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

		val fab = findViewById(R.id.fab) as FloatingActionButton
		fab.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
	}

	override fun onListFragmentInteraction(item: CocktailItem) {
		Toast.makeText(this, item.title + " tapped", Toast.LENGTH_SHORT).show()
	}


	/**
	 * A [FragmentPagerAdapter] that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
		private val PAGER_COUNT: Int = 2
		private val COCKTAIL_COLUMN_COUNT: Int = 2

		override fun getItem(position: Int): Fragment {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return if (position == 0) OrderFragment.Companion.newInstance(COCKTAIL_COLUMN_COUNT) else StatusFragment.Companion.newInstance()
		}

		override fun getCount(): Int {
			return PAGER_COUNT
		}
	}
}
