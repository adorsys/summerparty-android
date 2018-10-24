package de.adorsys.android.summerparty.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.adorsys.android.network.mutable.MutableCustomer
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.Repository
import de.adorsys.android.summerparty.Repository.user
import de.adorsys.android.summerparty.ui.PostFragment.Companion.REQUEST_CODE_CAMERA_CAPTURE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class MainActivity :
        BaseActivity(),
        PostFragment.OnGetPermissionsListener,
        PostFragment.OnShowProgressListener,
        FeedFragment.OnStartPostFragmentListener {

    private var feedFragment: FeedFragment? = null
    private var postFragment: PostFragment? = null

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
    private var userMenuItem: MenuItem? = null
    private var userDialog: AlertDialog? = null


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init views
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { startFeedFragment() }

        fragmentContainer = findViewById(R.id.fragment_container)
        progressBarContainer = findViewById(R.id.progressBarContainer)
        progressBar = findViewById(R.id.progressBar)
        viewContainer = findViewById(R.id.main_content)
        preferences = getSharedPreferences(KEY_PREFS_FILENAME, Context.MODE_PRIVATE)

        Repository.userLiveData.observe(this, Observer {
            updateUserMenuItem()
        })

        val userId = preferences.getString(KEY_USER_ID, null)
        userId?.let { Repository.getUser(it) }

        startFeedFragment()
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
        userMenuItem = menu.findItem(R.id.action_user)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_user) {
            createUserInfoDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION
                && resultCode == Activity.RESULT_OK) {
            postFragment?.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == REQUEST_CODE_CAMERA_CAPTURE
                && resultCode == Activity.RESULT_OK) {
            postFragment?.onActivityResult(requestCode, resultCode, data)
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
                    REQUEST_CODE_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionManager.handlePermissionsResult(this, grantResults, arrayOf(Manifest.permission.CAMERA)) {
            postFragment?.openCamera()
        }
    }

    override fun onStartPostFragment() {
        startPostMainFragment()
    }

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

    private fun startFeedFragment(): Boolean {
        toolbar.title = getString(R.string.feedTitle)
        if (feedFragment == null) {
            feedFragment = FeedFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, feedFragment!!).commit()
        return true
    }

    private fun startPostMainFragment(): Boolean {
        toolbar.title = getString(R.string.postTitle)
        if (postFragment == null) {
            postFragment = PostFragment()
        }
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, postFragment!!)
                .addToBackStack(postFragment?.javaClass.toString())
                .commit()
        return true
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
                }
            }
        }
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
        if (intent.getStringExtra(KEY_FIREBASE_TOKEN) != null) {
            firebaseToken = intent.getStringExtra(KEY_FIREBASE_TOKEN)
            startActivityForResult(Intent(this@MainActivity, CreateUserActivity::class.java), REQUEST_CODE_NAME)
        }
    }

    companion object {
        const val KEY_USER_ID = "preferences_key_user_id"
        const val KEY_USER_NAME = "preferences_key_user_name"
        const val KEY_PREFS_FILENAME = "de.adorsys.android.summerparty.prefs"
        const val KEY_FIREBASE_RECEIVER = "firebase_receiver"
        const val KEY_FIREBASE_RELOAD = "reload"
        const val KEY_FIREBASE_TOKEN = "firebase_token"
        const val REQUEST_CODE_CAMERA_PERMISSION: Int = 943
        const val REQUEST_CODE_NAME = 24
    }
}
