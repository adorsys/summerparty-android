package de.adorsys.android.summerparty.ui.adapter

import android.graphics.Bitmap
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import de.adorsys.android.shared.Post
import de.adorsys.android.shared.views.ImageUtils
import de.adorsys.android.summerparty.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class FeedAdapter(
        private var query: Query?,
        private val onAdapterChangedAction: (position: Int) -> Unit,
        private val onBindAction: (position: Int) -> Unit,
        private val onClickAction: ((imageReference: String) -> Unit)?) : RecyclerView.Adapter<FeedAdapter.PostViewHolder>(), EventListener<QuerySnapshot> {

    private val snapshots = mutableListOf<DocumentSnapshot>()
    private var listener: ListenerRegistration? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PostViewHolder(inflater.inflate(R.layout.item_post, parent, false))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(snapshots[position], position, onClickAction)
        onBindAction(position)
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
        onAdapterChangedAction(change.newIndex)
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


    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView = view.findViewById<ImageView>(R.id.imageView)
        private val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        private val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)

        fun bind(snapshot: DocumentSnapshot, position: Int, onClickAction: ((imageReference: String) -> Unit)?) {
            try {
                val post = snapshot.toObject(Post::class.java)
                imageView.setImageDrawable(null)

                if (!post?.imageReference.isNullOrEmpty()) {
                    if (onClickAction != null) {
                        imageView.setOnClickListener { onClickAction(post?.imageReference!!) }
                    }
                    ImageUtils.getImageFile(itemView.context, post?.imageReference, snapshot) { imageFile ->
                        launch {
                            val bitmap = ImageUtils.getScaledImage(imageView.measuredWidth.toFloat(), imageFile.path)
                            setBitmap(bitmap, position)
                        }
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    titleTextView?.text = Html.fromHtml(titleTextView.context.getString(R.string.image_shared_by, post?.name), Html.FROM_HTML_MODE_LEGACY)
                } else {
                    titleTextView?.text = Html.fromHtml(titleTextView.context.getString(R.string.image_shared_by, post?.name))
                }

                val text = post?.text
                if (text.isNullOrBlank()) {
                    descriptionTextView.visibility = View.GONE
                } else {
                    descriptionTextView.visibility = View.VISIBLE
                    descriptionTextView?.text = post?.text
                }
            } catch (e: Exception) {
                Log.e("TAG_THINGS", "Could not correctly parse post object for $snapshot")
            }
        }

        private fun setBitmap(bitmap: Bitmap?, position: Int) {
            launch(UI) {
                if (position == adapterPosition) {
                    if (bitmap == null) {
                        imageView.setImageDrawable(null)
                    } else {
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
}