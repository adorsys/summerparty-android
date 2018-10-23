package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import de.adorsys.android.shared.FirebaseProvider
import de.adorsys.android.summerparty.R
import de.adorsys.android.summerparty.ui.adapter.FeedAdapter
import kotlinx.android.synthetic.main.fragment_gallery.*


class FeedFragment : Fragment() {
    private lateinit var adapter: FeedAdapter
    private var currentPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
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