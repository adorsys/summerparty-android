package de.adorsys.android.summerparty.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import de.adorsys.android.shared.FirebaseProvider
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.ui.adapter.FeedAdapter
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment() {
    interface OnStartPostFragmentListener {
        fun onStartPostFragment()
    }

    private lateinit var adapter: FeedAdapter
    private lateinit var listener: OnStartPostFragmentListener
    private var currentPosition: Int = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as OnStartPostFragmentListener
        } catch (e: ClassCastException) {
            Log.e("FEED", "Your activity has to implement the OnStartPostFragmentListener")
            fragmentManager?.popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = FirebaseProvider.getFeed()
        adapter = FeedAdapter(
                query,
                { position ->
                    feedRecyclerView.scrollToPosition(position)
                },
                { position ->
                    currentPosition = position
                },
                null)

        adapter.startListening()

        feedRecyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(context, 1)
        feedRecyclerView.layoutManager = layoutManager

        val position = savedInstanceState?.getInt("position") ?: 0
        feedRecyclerView.postDelayed({
            layoutManager.scrollToPosition(position)
        }, 2000)

        val toolbar = activity!!.findViewById<Toolbar>(R.id.toolbar)
        toolbar.navigationIcon = null
        toolbar.setTitle(R.string.feedTitle)

        view.findViewById<View>(R.id.addPostButton).setOnClickListener {
            listener.onStartPostFragment()
        }
    }

    override fun onDetach() {
        super.onDetach()
        adapter.stopListening()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
        outState.putInt("position", currentPosition)
    }
}