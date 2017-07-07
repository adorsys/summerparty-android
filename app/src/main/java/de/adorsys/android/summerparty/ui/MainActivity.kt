package de.adorsys.android.summerparty.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.data.ApiManager
import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.Customer
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.data.mutable.MutableCustomer
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), CocktailFragment.OnListFragmentInteractionListener {
    companion object {
        val KEY_USER_ID = "preferences_key_user_id"
        val KEY_USER_NAME = "preferences_key_user_name"
        val PREFS_FILENAME = "de.adorsys.android.summerparty.prefs"

    }

    // TODO get real user from login instead of creating one
    private var user: Customer? = null
    private var viewPager: ViewPager? = null
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init views
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val tabLayout = findViewById(R.id.tabs) as TabLayout
        viewPager = findViewById(R.id.container) as ViewPager
        preferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

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
        // TODO create user via user login instead of using the mock content
        // Update adapter's cocktail list
        if (preferences!!.contains(KEY_USER_ID)) {
            ApiManager.INSTANCE.getCustomer(preferences!!.getString(KEY_USER_ID, null),
                    object : Callback<Customer> {
                        override fun onResponse(call: Call<Customer>?, response: Response<Customer>?) {
                            user = response?.body()
                            if (user == null) {
                                // backend has hard-reset the database
                                getPreferences(Context.MODE_PRIVATE).edit().clear().apply()
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

    override fun onListFragmentInteraction(item: Cocktail) {
        val viewContainer = findViewById(R.id.main_content)
        if (viewContainer != null && item.available) {
            val cocktail = ArrayList<String>(1)
            cocktail.add(item.id)

            val currentOrder = MutableOrder(cocktail, if (user == null) "" else (user as Customer).id)
            Snackbar.make(viewContainer, getString(R.string.order_cocktail, item.name), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_cart) {
                        if ((preferences as SharedPreferences).contains(MainActivity.KEY_USER_ID)) {
                            createOrder(currentOrder)
                        } else {
                            ApiManager.INSTANCE.createCustomer(MutableCustomer((preferences as SharedPreferences).getString(KEY_USER_NAME, ""), FirebaseInstanceId.getInstance().token),
                                    object : Callback<Customer> {
                                        override fun onResponse(call: Call<Customer>?, response: Response<Customer>?) {
                                            val customer = response?.body()
                                            (preferences as SharedPreferences).edit().putString(MainActivity.KEY_USER_ID, customer?.id).apply()
                                            createOrder(currentOrder)
                                        }

                                        override fun onFailure(call: Call<Customer>?, t: Throwable?) {
                                            Log.i("TAG_USER", t?.message)
                                        }
                                    })
                        }
                    }
                    .show()
        } else if (viewContainer != null) {
            Snackbar.make(viewContainer, getString(R.string.cocktail_out_of_stock, item.name), Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    private fun createOrder(currentOrder: MutableOrder) {
        ApiManager.INSTANCE.createOrder(
                currentOrder,
                object : Callback<Void> {
                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        if (response != null && response.isSuccessful) {
                            Toast.makeText(this@MainActivity, "Successfully created order", Toast.LENGTH_SHORT).show()
                            if (user != null) {
                                getOrdersForUser()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        Log.i("TAG_ORDER_CREATE", t?.message)
                    }
                })
    }
}
