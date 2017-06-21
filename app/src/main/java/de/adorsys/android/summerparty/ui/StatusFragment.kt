package de.adorsys.android.summerparty.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.summerparty.R


class StatusFragment : Fragment() {

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater!!.inflate(R.layout.fragment_status, container, false)
	}

	companion object {
		fun newInstance(): StatusFragment {
			return StatusFragment()
		}
	}
}
