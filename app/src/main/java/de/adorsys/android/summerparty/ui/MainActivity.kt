package de.adorsys.android.summerparty.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.media.session.MediaButtonReceiver.handleIntent
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import de.adorsys.android.network.ApiManager
import de.adorsys.android.network.Cocktail
import de.adorsys.android.network.Customer
import de.adorsys.android.network.Order
import de.adorsys.android.network.mutable.MutableCustomer
import de.adorsys.android.summerparty.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import de.adorsys.android.summerparty.R.id.bottom_navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class MainActivity : BaseActivity(), CocktailFragment.OnListFragmentInteractionListener, CocktailMainFragment.OnGetUserCocktailsListener, PostFragment.OnGetPermissionsListener {
    private var cocktailMainFragment: CocktailMainFragment? = null
    private var postFragment: PostFragment? = null

    companion object {
        const val KEY_USER_ID = "preferences_key_user_id"
        const val KEY_USER_NAME = "preferences_key_user_name"
        const val KEY_PREFS_FILENAME = "de.adorsys.android.summerparty.prefs"
        const val KEY_FIREBASE_RECEIVER = "firebase_receiver"
        const val KEY_FIREBASE_RELOAD = "reload"
        const val KEY_FIREBASE_TOKEN = "firebase_token"
        const val KEY_FIRST_START = "first_start"
        const val REQUEST_CODE_CART = 23
        const val REQUEST_CODE_CAMERA: Int = 942
        const val REQUEST_CODE_NAME = 24
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleIntent(intent, true)
        }
    }

    private var user: Customer? = null
    private var progressBar: View? = null
    private var viewContainer: View? = null
    private var cartMenuItem: MenuItem? = null
    private var userMenuItem: MenuItem? = null
    private var cartOptionsItemCount: TextView? = null
    private var preferences: SharedPreferences? = null
    private var firebaseToken: String? = null
    private var userDialog: AlertDialog? = null
    private var notificationDialog: AlertDialog? = null
    private var fallbackUserCreation = false
    private lateinit var fragmentContainer: FrameLayout


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init views
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        fragmentContainer = findViewById(R.id.fragment_container)
        progressBar = findViewById(R.id.progressBar)
        viewContainer = findViewById(R.id.main_content)
        preferences = getSharedPreferences(KEY_PREFS_FILENAME, Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)
        buildCocktailMainFragment()

        if (preferences!!.contains(KEY_USER_ID)) {
            getUser()
        } else if (firstStart()) {
            progressBar?.visibility = View.VISIBLE
        }

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cocktail_order -> buildCocktailMainFragment()
                R.id.feed -> buildPostMainFragment()
            }
            true
        }
    }

    private fun buildCocktailMainFragment() {

        toolbar.title = getString(R.string.app_name)
        if (cocktailMainFragment == null) {
            cocktailMainFragment = CocktailMainFragment()
        }
        getCocktails()
        getOrdersForUser(false)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, cocktailMainFragment).commit()
    }

    private fun buildPostMainFragment(){
        toolbar.setTitle(getString(R.string.postTitle))
        if (postFragment == null) {
            postFragment = PostFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, postFragment).commit()
    }

    override fun onResume() {
        super.onResume()
        getCocktails()
        getOrdersForUser(false)
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver((messageReceiver),
                IntentFilter(KEY_FIREBASE_RECEIVER))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        cartMenuItem = menu.findItem(R.id.action_cart)
        userMenuItem = menu.findItem(R.id.action_user)
        cartMenuItem?.setActionView(R.layout.view_action_cart)
        val cartOptionsItemContainer = cartMenuItem?.actionView as ViewGroup
        cartOptionsItemContainer.setOnClickListener {
            if (preferences!!.contains(KEY_USER_ID)) {
                openCartActivity()
            } else {
                fallbackUserCreation = true
                firebaseToken = FirebaseInstanceId.getInstance().token
                startActivityForResult(Intent(this, CreateUserActivity::class.java), REQUEST_CODE_NAME)
            }
        }
        cartOptionsItemCount = cartOptionsItemContainer.findViewById(R.id.action_cart_count_text) as TextView
        updateCartMenuItem()
        updateUserMenuItem()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_user) {
            createUserInfoDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK){
            postFragment?.onActivityResult(requestCode, resultCode, data)
        }


        if (requestCode == REQUEST_CODE_CART && resultCode == Activity.RESULT_OK) {
            cocktailMainFragment?.clearCocktailList()
            updateCartMenuItem()
            if (!fallbackUserCreation) {
                getOrdersForUser(true)
            }
        }

        if (requestCode == REQUEST_CODE_NAME && resultCode == Activity.RESULT_OK && data != null && !TextUtils.isEmpty(firebaseToken)) {
            createAndPersistUser(data.getStringExtra(CreateUserActivity.KEY_USERNAME), firebaseToken)
        } else if (requestCode == REQUEST_CODE_NAME && resultCode == Activity.RESULT_OK
                || requestCode == REQUEST_CODE_NAME && resultCode == Activity.RESULT_CANCELED) {
            preferences!!.edit().putBoolean(KEY_FIRST_START, true).apply()
            finish()
        }
    }

    override fun onGetPermission() {
        if (PermissionManager.permissionPending( applicationContext, android.Manifest.permission.CAMERA)) {
            PermissionManager.requestPermission(
                    this,
                    android.Manifest.permission.CAMERA,
                    REQUEST_CODE_CAMERA)
        }
    }

    override fun onGetUserCocktails() {
        getOrdersForUser(false)
    }

    override fun onListFragmentInteraction(item: Cocktail) {
        if (viewContainer != null && item.available) {
            cocktailMainFragment?.addToCocktailList(item)
            updateCartMenuItem()
        } else if (viewContainer != null) {
            Snackbar.make(viewContainer!!, getString(R.string.cocktail_out_of_stock, item.name), Snackbar.LENGTH_LONG).show()
        }
    }


    private fun openCartActivity() {
        val intent = Intent(this@MainActivity, CartActivity::class.java)
        intent.putExtra(CartActivity.EXTRA_COCKTAILS, cocktailMainFragment?.getCocktailList())
        intent.putExtra(CartActivity.EXTRA_USER_ID, user?.id)
        this@MainActivity.startActivityForResult(intent, REQUEST_CODE_CART)
    }

    private fun createAndPersistUser(username: String, firebaseToken: String?) {
        Log.d("TAG_USER", username)
        launch {
            try {
                val response = ApiManager.createCustomer(MutableCustomer(username, firebaseToken)).await()
                if (response.isSuccessful) {
                    val customer = response.body()
                    launch(UI) {
                        (preferences as SharedPreferences).edit().putString(MainActivity.KEY_USER_ID, customer?.id).apply()
                        (preferences as SharedPreferences).edit().putString(MainActivity.KEY_USER_NAME, customer?.name).apply()
                        launch(UI) {
                            user = customer
                            updateUserMenuItem()
                            if (fallbackUserCreation) {
                                openCartActivity()
                                fallbackUserCreation = false
                            }
                        }
                    }
                } else {
                    Log.i("TAG_USER", response.message())
                }
            } catch (e: Exception) {
                Log.i("TAG_USER", e.message)
            }
        }
    }

    private fun getCocktails() {
        launch {
            try {
                val response = ApiManager.getCocktails().await()
                if (response.isSuccessful) {
                    val cocktailResponse: List<Cocktail>? = response.body()
                    // Update adapter's cocktail list
                    cocktailResponse?.let {
                        launch(UI) {
                            cocktailMainFragment?.notifyDataSetChanged(cocktailList = it)
                        }
                    }
                } else {
                    Log.i("TAG_COCKTAILS", response.message())
                }
            } catch (e: Exception) {
                Log.i("TAG_COCKTAILS", e.message)
            }
        }
    }

    private fun getUser() {
        // Update adapter's cocktail list
        launch {
            try {
                val response = ApiManager.getCustomer(preferences?.getString(KEY_USER_ID, null) ?: "").await()
                if (response.isSuccessful) {
                    user = response.body()
                    updateUserMenuItem()
                    launch(UI) {
                        if (user == null) {
                            // backend has hard-reset the database
                            (preferences as SharedPreferences).edit().clear().apply()
                            getUser()
                        }
                        (preferences as SharedPreferences).edit().putString(KEY_USER_NAME, this@MainActivity.user?.name).apply()
                    }
                } else {
                    Log.i("TAG_USER", response.message())
                }
            } catch (e: Exception) {
                Log.i("TAG_USER", e.message)
            }
        }
    }

    private fun getOrdersForUser(goToOrders: Boolean) {
        if (user == null) {
            return
        }
        launch {
            try {
                val response = ApiManager.getOrdersForCustomer(user!!.id).await()
                if (response.isSuccessful) {
                    val cocktailResponse: List<Order>? = response.body()
                    // Update adapter's order list
                    cocktailResponse?.let {
                        launch(UI) { cocktailMainFragment?.notifyDataSetChanged(orderList = it, goToOrders = goToOrders) }
                    }
                } else {
                    Log.i("TAG_CUSTOMER_ORDERS", response.message())
                }
            } catch (e: Exception) {
                Log.i("TAG_CUSTOMER_ORDERS", e.message)
            }
        }
    }

    private fun updateCartMenuItem() {
        cartMenuItem?.isVisible = cocktailMainFragment?.getCocktailList()?.isNotEmpty() ?: false
        cartOptionsItemCount?.text = cocktailMainFragment?.getCocktailList()?.size.toString()
    }

    private fun updateUserMenuItem() {
        userMenuItem?.isVisible = user != null
    }

    private fun createUserInfoDialog() {
        if (userDialog == null || !(userDialog as AlertDialog).isShowing) {
            val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.user_content_title)
                    .setMessage(getString(R.string.user_content_text, user?.name, user?.id))
                    .setPositiveButton(android.R.string.ok, null)
            userDialog = dialogBuilder.create()
            userDialog?.show()
        }
    }

    private fun firstStart(): Boolean {
        return if ((preferences as SharedPreferences).contains(KEY_FIRST_START)) {
            false
        } else {
            (preferences as SharedPreferences).edit().putBoolean(KEY_FIRST_START, true).apply()
            true
        }
    }

    private fun handleIntent(intent: Intent?, showDialog: Boolean) {
        progressBar?.visibility = View.GONE
        if (intent == null) {
            return
        }
        if (intent.getBooleanExtra(KEY_FIREBASE_RELOAD, false)) {
            if (showDialog && (notificationDialog == null || !(notificationDialog as AlertDialog).isShowing)) {
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                        .setIcon(R.drawable.ic_cocktail_icon)
                        .setTitle(R.string.notification_content_title)
                        .setMessage(R.string.notification_content_text)
                        .setPositiveButton(android.R.string.ok) { _, _ -> getOrdersForUser(true) }
                notificationDialog = dialogBuilder.create()
                notificationDialog?.show()
            } else if (!showDialog) {
                getOrdersForUser(true)
            }
        }
        if (intent.getStringExtra(KEY_FIREBASE_TOKEN) != null) {
            firebaseToken = intent.getStringExtra(KEY_FIREBASE_TOKEN)
            startActivityForResult(Intent(this@MainActivity, CreateUserActivity::class.java), REQUEST_CODE_NAME)
        }
    }
}
