package com.local.amjbc

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class UpdateTimings : Fragment() {

    var Fajar: EditText? = null
    var Zuhr: EditText? = null
    var Asar: EditText? = null
    var Maghrib: EditText? = null
    var Isha: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_update_timings_center, container, false)

        (activity as MainActivity).actionBar?.title = "Update Timings"

        setHasOptionsMenu(true)

        Fajar = rootView.findViewById<View>(R.id.fajr) as EditText
        Zuhr = rootView.findViewById<View>(R.id.zuhr) as EditText
        Asar = rootView.findViewById<View>(R.id.asar) as EditText
        Maghrib = rootView.findViewById<View>(R.id.maghrib) as EditText
        Isha = rootView.findViewById<View>(R.id.isha) as EditText

        return rootView

    }
}
