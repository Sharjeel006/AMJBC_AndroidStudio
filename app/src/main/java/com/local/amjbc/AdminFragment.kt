package com.local.amjbc

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class AdminFragment : Fragment() {

    var add: Button? = null
    var timings: Button? = null
    var username: EditText? = null
    var pass: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_admin_options, container, false)

        setHasOptionsMenu(false)

        username = rootView.findViewById<View>(R.id.username) as EditText
        pass = rootView.findViewById<View>(R.id.password) as EditText
        timings = rootView.findViewById<View>(R.id.timingsupdate) as Button
        add = rootView.findViewById<View>(R.id.button1) as Button
        add!!.setOnClickListener(onClickListener)
        timings!!.setOnClickListener(onClickListener2)

        setHasOptionsMenu(true)
        return rootView

    }


    private val onClickListener = OnClickListener {
        val newFragment = AddEventFragment()
        val transaction = fragmentManager.beginTransaction()

        activity.overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
        transaction.replace(R.id.content_frame, newFragment)
        transaction.addToBackStack(null)

        transaction.commit()
    }


    private val onClickListener2 = OnClickListener {
        val newFragment2 = UpdateTimings()
        val transaction2 = fragmentManager.beginTransaction()

        activity.overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
        transaction2.replace(R.id.content_frame, newFragment2)
        transaction2.addToBackStack(null)

        transaction2.commit()
    }
}
