package de.adorsys.android.summerpartysocial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.*


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : AppCompatActivity() {
    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val feedFragment = FeedFragment()
        val orderStateFragment = OrderStateFragment()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_start, feedFragment)
                .replace(R.id.container_end, orderStateFragment)
                .commit()

        setContentView(R.layout.activity_main)
    }

    class FeedFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_feed, container)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

        }

        class FirestoreAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getItemCount(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onEvent(feedSnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                // Handle errors
                if (e != null) {
                    Log.w("MAIN ACTIVITY", "onEvent:error", e)
                    return
                }

                if (feedSnapshot == null) {
                    return
                }

                // Dispatch the event
                for (change in feedSnapshot.documentChanges) {
                    // Snapshot of the changed document
                    val snapshot = change.document

                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                        }
                        DocumentChange.Type.MODIFIED -> {
                        }
                        DocumentChange.Type.REMOVED -> {
                        }
                    }
                    // TODO: handle document added
                    // TODO: handle document modified
                    // TODO: handle document removed
                    // TODO continue here: https://codelabs.developers.google.com/codelabs/firestore-android/index.html#4
                }
            }
        }

        data class Post(val user: String?, val image: String?, val text: String?)
    }

    class OrderStateFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_order_state, container)
        }
    }
}
