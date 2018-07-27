package de.adorsys.android.summerparty.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.*
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import de.adorsys.android.network.Cocktail
import de.adorsys.android.network.mutable.MutableCustomer
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.Repository
import de.adorsys.android.summerparty.Repository.user
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : BaseActivity(), CocktailFragment.OnListFragmentInteractionListener, PostFragment.OnGetPermissionsListener, PostFragment.OnShowProgressListener {
    override fun showProgress(show: Boolean, progress: Int?) {
        if (show) {
            bottom_navigation.visibility = View.INVISIBLE
            progressBarContainer.visibility = View.VISIBLE
            progress?.let { progressBar.progress = it }
        } else {
            bottom_navigation.visibility = View.VISIBLE
            progressBarContainer.visibility = View.GONE
        }
    }

    private var cocktailMainFragment: CocktailMainFragment? = null
    private var postFragment: PostFragment? = null

    companion object {
        const val KEY_USER_ID = "preferences_key_user_id"
        const val KEY_USER_NAME = "preferences_key_user_name"
        const val KEY_PREFS_FILENAME = "de.adorsys.android.summerparty.prefs"
        const val KEY_FIREBASE_RECEIVER = "firebase_receiver"
        const val KEY_FIREBASE_RELOAD = "reload"
        const val KEY_FIREBASE_TOKEN = "firebase_token"
        const val REQUEST_CODE_CART = 23
        const val REQUEST_CODE_CAMERA: Int = 942
        const val REQUEST_CODE_NAME = 24
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleIntent(intent, true)
        }
    }

    private lateinit var preferences: SharedPreferences
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarContainer: FrameLayout
    private lateinit var fragmentContainer: FrameLayout

    private var firebaseToken: String? = null
    private var viewContainer: View? = null
    private var cartMenuItem: MenuItem? = null
    private var userMenuItem: MenuItem? = null
    private var cartOptionsItemCount: TextView? = null
    private var userDialog: AlertDialog? = null
    private var notificationDialog: AlertDialog? = null


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
        progressBarContainer = findViewById(R.id.progressBarContainer)
        progressBar = findViewById(R.id.progressBar)
        viewContainer = findViewById(R.id.main_content)
        preferences = getSharedPreferences(KEY_PREFS_FILENAME, Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cocktail_order -> return@setOnNavigationItemSelectedListener startCocktailMainFragment()
                R.id.feed -> return@setOnNavigationItemSelectedListener startPostMainFragment()
            }
            false
        }

        Repository.cocktailsLiveData.observe(this, Observer {
            progressBar.visibility = View.GONE
        })

        Repository.pendingCocktailsLiveData.observe(this, Observer {
            updateCartMenuItem()
        })

        Repository.userLiveData.observe(this, Observer {
            updateUserMenuItem()
            launch {
                it?.id?.let { userId ->
                    Repository.fetchStartupData(userId).await()
                }
            }
        })

        val userId = preferences.getString(KEY_USER_ID, null)
        userId?.let { Repository.getUser(it) }

        startCocktailMainFragment()
    }

    override fun onResume() {
        super.onResume()
        getCocktails()
        getOrdersForUser()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver((messageReceiver),
                IntentFilter(KEY_FIREBASE_RECEIVER))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        cartMenuItem = menu.findItem(R.id.action_cart)
        userMenuItem = menu.findItem(R.id.action_user)
        cartMenuItem?.setActionView(R.layout.view_action_cart)
        val cartOptionsItemContainer = cartMenuItem?.actionView as ViewGroup
        cartOptionsItemContainer.setOnClickListener {
            if (Repository.user != null) {
                openCartActivity()
            }
        }
        cartOptionsItemCount = cartOptionsItemContainer.findViewById(R.id.action_cart_count_text) as TextView

        updateCartMenuItem()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_user) {
            createUserInfoDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAMERA
                && resultCode == Activity.RESULT_OK) {
            postFragment?.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == REQUEST_CODE_CART
                && resultCode == Activity.RESULT_OK) {
            getOrdersForUser(true)
        }

        if (requestCode == REQUEST_CODE_NAME
                && resultCode == Activity.RESULT_OK
                && data != null) {
            createAndPersistUser(data.getStringExtra(CreateUserActivity.KEY_USERNAME))
        }
    }

    override fun onRequestPermission() {
        if (PermissionManager.permissionPending(applicationContext, android.Manifest.permission.CAMERA)) {
            PermissionManager.requestPermission(
                    this,
                    android.Manifest.permission.CAMERA,
                    REQUEST_CODE_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionManager.handlePermissionsResult(this, grantResults, arrayOf(Manifest.permission.CAMERA)) {
            postFragment?.openCamera()
        }
    }

    override fun onListFragmentInteraction(item: Cocktail) {
        if (viewContainer != null && item.available) {
            Repository.addToPendingCocktails(item)
        } else if (viewContainer != null) {
            Snackbar.make(viewContainer!!, getString(R.string.cocktail_out_of_stock, item.name), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun startCocktailMainFragment(): Boolean {
        toolbar.title = getString(R.string.app_name)
        if (cocktailMainFragment == null) {
            cocktailMainFragment = CocktailMainFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, cocktailMainFragment).commit()
        return true
    }

    private fun startPostMainFragment(): Boolean {
        toolbar.title = getString(R.string.postTitle)
        if (postFragment == null) {
            postFragment = PostFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, postFragment).commit()
        return true
    }

    private fun openCartActivity() {
        val intent = Intent(this@MainActivity, CartActivity::class.java)
        this@MainActivity.startActivityForResult(intent, REQUEST_CODE_CART)
    }

    private fun createAndPersistUser(username: String) {
        Log.d("TAG_USER", username)
        launch {
            firebaseToken?.let { token ->
                Repository.createUser(MutableCustomer(username, token)) {
                    launch(UI) {
                        preferences.edit().putString(MainActivity.KEY_USER_ID, it?.id).apply()
                        preferences.edit().putString(MainActivity.KEY_USER_NAME, it?.name).apply()
                    }
                    it?.id?.let { userId ->
                        val success = Repository.fetchStartupData(userId)
                        Log.d("TAG_STARTUP", "fetching startup data successful: $success")
                    }
                }
            }
        }
    }

    private fun getOrdersForUser(goToOrders: Boolean = false, forceReload: Boolean = false) {
        Repository.fetchOrders({
            launch(UI) {
                if (goToOrders) {
                    cocktailMainFragment?.goToOrders()
                }
            }
        }, forceReload)
    }

    private fun getCocktails(forceReload: Boolean = false) = Repository.fetchCocktails({}, forceReload)

    private fun updateCartMenuItem() {
        val list = Repository.pendingCocktailList
        cartMenuItem?.isVisible = list.isNotEmpty()
        cartOptionsItemCount?.text = list.size.toString()
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

    private fun handleIntent(intent: Intent?, showDialog: Boolean) {
        progressBar.visibility = View.GONE
        if (intent == null) {
            return
        }
        if (intent.getBooleanExtra(KEY_FIREBASE_RELOAD, false)) {
            if (showDialog && (notificationDialog == null || !(notificationDialog as AlertDialog).isShowing)) {
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                        .setIcon(R.drawable.ic_cocktail_icon)
                        .setTitle(R.string.notification_content_title)
                        .setMessage(R.string.notification_content_text)
                        .setPositiveButton(android.R.string.ok) { _, _ -> getOrdersForUser(true, true) }
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
