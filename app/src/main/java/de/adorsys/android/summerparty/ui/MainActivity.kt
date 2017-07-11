package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.iid.FirebaseInstanceId
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.ApiManager
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.Customer
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.data.mutable.MutableCustomer
import de.adorsys.android.summerparty.ui.adapter.SectionsPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), CocktailFragment.OnListFragmentInteractionListener {
    companion object {
        val KEY_USER_ID = "preferences_key_user_id"
        val KEY_USER_NAME = "preferences_key_user_name"
        val KEY_PREFS_FILENAME = "de.adorsys.android.summerparty.prefs"
        val REQUEST_CODE_CART = 23
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras.getBoolean("reload")) {
                goToOrdersAndRefresh()
            }
        }
    }

    private var user: Customer? = null
    private var viewPager: ViewPager? = null
    private var viewContainer: View? = null
    private var menuItem: MenuItem? = null
    private var cartOptionsItemCount: TextView? = null

    private var preferences: SharedPreferences? = null
    private var pendingCocktails: ArrayList<Cocktail> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init views
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val tabLayout = findViewById(R.id.tabs) as TabLayout
        viewPager = findViewById(R.id.container) as ViewPager
        viewContainer = findViewById(R.id.main_content)
        preferences = getSharedPreferences(KEY_PREFS_FILENAME, Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        // Set up the ViewPager with the sections adapter.
        viewPager!!.adapter = sectionsPagerAdapter
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        getUser()
        getCocktails()
    }

    private fun goToOrdersAndRefresh() {
        viewPager!!.setCurrentItem(1)
        //TODO refresh order adapter
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                IntentFilter("NotificationBroadcast"))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private fun getCocktails() {
        ApiManager.INSTANCE.getCocktails(
                object : Callback<List<Cocktail>> {
                    override fun onResponse(call: Call<List<Cocktail>>?, response: Response<List<Cocktail>>?) {
                        val cocktailResponse: List<Cocktail>? = response?.body()
                        // Update adapter's cocktail list
                        cocktailResponse?.let { (viewPager?.adapter as SectionsPagerAdapter).setCocktails(it) }
                        viewPager?.adapter?.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<List<Cocktail>>?, t: Throwable?) {
                        Log.i("TAG_COCKTAILS", t?.message)
                    }
                })
    }

    private fun getUser() {
        // TODO create user via user login instead of using the mock content -> delete mock package and content
        // Update adapter's cocktail list
        if (preferences!!.contains(KEY_USER_ID)) {
            ApiManager.INSTANCE.getCustomer(preferences!!.getString(KEY_USER_ID, null),
                    object : Callback<Customer> {
                        override fun onResponse(call: Call<Customer>?, response: Response<Customer>?) {
                            user = response?.body()
                            if (user == null) {
                                // backend has hard-reset the database
                                (preferences as SharedPreferences).edit().clear().apply()
                                getUser()
                                return
                            }
                            (preferences as SharedPreferences).edit().putString(KEY_USER_NAME, this@MainActivity.user?.name).apply()
                            getOrdersForUser()
                        }

                        override fun onFailure(call: Call<Customer>?, t: Throwable?) {
                            Log.i("TAG_USER", t?.message)
                        }
                    })
        } else {
            if (FirebaseInstanceId.getInstance().token != null) {
                ApiManager.INSTANCE.createCustomer(MutableCustomer((preferences as SharedPreferences).getString(KEY_USER_NAME, " "), FirebaseInstanceId.getInstance().token),
                        object : Callback<Customer> {
                            override fun onResponse(call: Call<Customer>?, response: Response<Customer>?) {
                                user = response?.body()
                                (preferences as SharedPreferences).edit().putString(MainActivity.KEY_USER_ID, user?.id).apply()
                            }

                            override fun onFailure(call: Call<Customer>?, t: Throwable?) {
                                Log.i("TAG_USER", t?.message)
                            }
                        })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent!!.getBooleanExtra("reload", false)) {
            goToOrdersAndRefresh()
        }
    }

    private fun getOrdersForUser() {
        if (user == null) {
            return
        }
        ApiManager.INSTANCE.getOrdersForCustomer(user!!.id,
                object : Callback<List<Order>> {
                    override fun onResponse(call: Call<List<Order>>?, response: Response<List<Order>>?) {
                        val cocktailResponse: List<Order>? = response?.body()
                        // Update adapter's order list
                        cocktailResponse?.let { (viewPager?.adapter as SectionsPagerAdapter).setOrders(it) }
                        viewPager?.adapter?.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<List<Order>>?, t: Throwable?) {
                        Log.i("TAG_CUSTOMER_ORDERS", t?.message)
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuItem = menu.findItem(R.id.action_cart)
        MenuItemCompat.setActionView(menuItem, R.layout.view_action_cart)
        val cartOptionsItemContainer = MenuItemCompat.getActionView(menuItem) as ViewGroup
        cartOptionsItemContainer.setOnClickListener {
            val intent = Intent(this@MainActivity, CartActivity::class.java)
            intent.putExtra(CartActivity.EXTRA_COCKTAILS, pendingCocktails)
            intent.putExtra(CartActivity.EXTRA_USER_ID, user?.id)
            this@MainActivity.startActivityForResult(intent, REQUEST_CODE_CART)
        }
        cartOptionsItemCount = cartOptionsItemContainer.findViewById(R.id.action_cart_count_text) as TextView
        updateCartMenuItem()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CART && resultCode == Activity.RESULT_OK) {
            pendingCocktails.clear()
            updateCartMenuItem()
            getOrdersForUser()
        }
    }

    override fun onListFragmentInteraction(item: Cocktail) {
        if (viewContainer != null && item.available) {


            if ((preferences as SharedPreferences).contains(MainActivity.KEY_USER_ID)) {
                val cocktail = ArrayList<String>(1)
                cocktail.add(item.id)

                pendingCocktails.add(item)
                updateCartMenuItem()
            } else {
                ApiManager.INSTANCE.createCustomer(MutableCustomer((preferences as SharedPreferences).getString(KEY_USER_NAME, ""), FirebaseInstanceId.getInstance().token),
                        object : Callback<Customer> {
                            override fun onResponse(call: Call<Customer>?, response: Response<Customer>?) {
                                val customer = response?.body()
                                (preferences as SharedPreferences).edit().putString(MainActivity.KEY_USER_ID, customer?.id).apply()
                                val cocktail = ArrayList<String>(1)
                                cocktail.add(item.id)

                                pendingCocktails.add(item)
                                updateCartMenuItem()
                            }

                            override fun onFailure(call: Call<Customer>?, t: Throwable?) {
                                Log.i("TAG_USER", t?.message)
                            }
                        })
            }
        } else if (viewContainer != null) {
            Snackbar.make(viewContainer!!, getString(R.string.cocktail_out_of_stock, item.name), Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    fun updateCartMenuItem() {
        menuItem?.isVisible = !pendingCocktails.isEmpty()
        cartOptionsItemCount?.text = pendingCocktails.size.toString()
    }
}
