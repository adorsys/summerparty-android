package de.adorsys.android.summerpartysocial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_feed.*


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val feedFragment = FeedFragment()
        val orderStateFragment = OrderStateFragment()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_start, feedFragment)
                .replace(R.id.container_end, orderStateFragment)
                .commit()
    }

    class FeedFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_feed, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            FirebaseFirestore.setLoggingEnabled(true)
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build()
            firestore.firestoreSettings = settings

            val query = firestore.collection("summerparty")

            val adapter = FeedAdapter(query)

            feed_recycler_view.adapter = adapter
        }

        override fun onStart() {
            super.onStart()
            (feed_recycler_view.adapter as FeedAdapter).startListening()
        }

        override fun onStop() {
            super.onStop()
            (feed_recycler_view.adapter as FeedAdapter).stopListening()
        }

        open class FeedAdapter(private var query: Query?) : RecyclerView.Adapter<PostViewHolder>(), EventListener<QuerySnapshot> {
            private val snapshots = mutableListOf<DocumentSnapshot>()
            private var listener: ListenerRegistration? = null

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                // TODO continue here: https://github.com/firebase/quickstart-android/blob/master/firestore/app/src/main/java/com/google/firebase/example/fireeats/MainActivity.java
                return PostViewHolder(inflater.inflate(R.layout.item_post, parent, false))
            }

            override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
                holder.bind(snapshots[position])
            }

            override fun onEvent(feedSnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                // Handle errors
                if (e != null) {
                    Log.w(javaClass.canonicalName, "onEvent:error", e)
                    return
                }

                if (feedSnapshot == null) {
                    return
                }

                // Dispatch the event
                for (change in feedSnapshot.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> onDocumentAdded(change)
                        DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                        DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
                    }
                }
            }

            fun startListening() {
                if (query != null && listener == null) {
                    listener = query?.addSnapshotListener(this)
                }
            }

            fun stopListening() {
                if (listener != null) {
                    listener?.remove()
                    listener = null
                }

                snapshots.clear()
                notifyDataSetChanged()
            }

            override fun getItemCount(): Int {
                return snapshots.size
            }


            private fun onDocumentAdded(change: DocumentChange) {
                snapshots.add(change.newIndex, change.document)
                notifyItemInserted(change.newIndex)
            }

            private fun onDocumentModified(change: DocumentChange) {
                if (change.oldIndex == change.newIndex) {
                    // Item changed but remained in same position
                    snapshots[change.oldIndex] = change.document
                    notifyItemChanged(change.oldIndex)
                } else {
                    // Item changed and changed position
                    snapshots.removeAt(change.oldIndex)
                    snapshots.add(change.newIndex, change.document)
                    notifyItemMoved(change.oldIndex, change.newIndex)
                }
            }

            private fun onDocumentRemoved(change: DocumentChange) {
                snapshots.removeAt(change.oldIndex)
                notifyItemRemoved(change.oldIndex)
            }
        }

        class PostViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
            private val imageView = view?.findViewById<ImageView>(R.id.imageView)
            private val titleTextView = view?.findViewById<TextView>(R.id.titleTextView)
            private val descriptionTextView = view?.findViewById<TextView>(R.id.descriptionTextView)


            fun bind(snapshot: DocumentSnapshot) {
                val post = snapshot.toObject(Post::class.java)
                // TODO imageString to image
                titleTextView?.text = "Foto geteilt von ${post?.username}"
                descriptionTextView?.text = post?.text
            }
        }

    }

    class OrderStateFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_order_state, container, false)
        }
    }
}
