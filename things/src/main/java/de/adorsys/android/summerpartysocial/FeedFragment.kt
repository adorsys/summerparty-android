package de.adorsys.android.summerpartysocial

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_feed.*
import java.io.ByteArrayOutputStream




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

        val query = firestore.collection("summerparty").orderBy("timestamp", Query.Direction.DESCENDING)
        val adapter = FeedAdapter(query)
        feed_recycler_view.adapter = adapter
        val layoutManager = GridLayoutManager(context, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(position) {
                    0 -> 2
                    else -> 1
                }
            }
        }
        feed_recycler_view.layoutManager = layoutManager
    }

    override fun onStart() {
        super.onStart()
        (feed_recycler_view.adapter as FeedAdapter).startListening()
    }

    override fun onStop() {
        super.onStop()
        (feed_recycler_view.adapter as FeedAdapter).stopListening()
    }

    class FeedAdapter(private var query: Query?) : RecyclerView.Adapter<PostViewHolder>(), EventListener<QuerySnapshot> {
        private val snapshots = mutableListOf<DocumentSnapshot>()
        private var listener: ListenerRegistration? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return PostViewHolder(inflater.inflate(R.layout.item_post, parent, false))
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(snapshots[position])
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> TYPE_BIG
                1 -> TYPE_MEDIUM
                else -> TYPE_SMALL
            }
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

        companion object {
            private const val TYPE_BIG: Int = 0
            private const val TYPE_MEDIUM: Int = 1
            private const val TYPE_SMALL: Int = 2
        }
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView = view.findViewById<ImageView>(R.id.imageView)
        private val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        private val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)


        fun bind(snapshot: DocumentSnapshot) {
            val post = snapshot.toObject(Post::class.java)
            val bitmap = post?.image?.let { getBitmapFromEncodedBytes(it) }
            bitmap?.let { imageView.setImageBitmap(null) }
            titleTextView?.text = titleTextView.context.getString(R.string.image_shared_by, post?.name)
            descriptionTextView?.text = post?.text
        }

        private fun getEncodedBytesFromBitmap(bitmap: Bitmap): String? {
            return try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
            } catch (e: Exception) {
                null
            }
        }

        private fun getBitmapFromEncodedBytes(encodedBytes: String): Bitmap? {
            return try {
                val decodedBytes = Base64.decode(encodedBytes, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        }
    }

}