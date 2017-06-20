package de.adorsys.android.summerparty

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class OrderFragment : Fragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater!!.inflate(R.layout.fragment_order, container, false)
	}

	companion object {
		fun newInstance(): OrderFragment {
			return OrderFragment()
		}
	}
}
