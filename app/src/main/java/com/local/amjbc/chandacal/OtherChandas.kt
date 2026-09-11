package com.local.amjbc.chandacal

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.local.amjbc.R

class OtherChandas : Fragment() {

    var add: Button? = null
    var cancel: Button? = null
    var chanda_tj: EditText? = null
    var chanda_wj: EditText? = null
    var chanda_ef: EditText? = null
    var chanda_fitrana: EditText? = null
    var chanda_sadqa: EditText? = null
    var tj: Int = 0
    var wj: Int = 0
    var ef: Int = 0
    var fit: Int = 0
    var sad: Int = 0
    var sp: SharedPreferences? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        activity!!.overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        val rootView = inflater.inflate(R.layout.other_chandas, container, false)
        val actionbar = activity!!.actionBar
        actionbar?.title = "Other Chandas"
        setHasOptionsMenu(false)

        sp = PreferenceManager.getDefaultSharedPreferences(activity)

        chanda_tj = rootView.findViewById<View>(R.id.tj) as EditText
        chanda_wj = rootView.findViewById<View>(R.id.wj) as EditText
        chanda_ef = rootView.findViewById<View>(R.id.ef) as EditText
        chanda_fitrana = rootView.findViewById<View>(R.id.fit) as EditText
        chanda_sadqa = rootView.findViewById<View>(R.id.sad) as EditText

        add = rootView.findViewById<View>(R.id.add) as Button
        add!!.setOnClickListener { addChandas() }

        cancel = rootView.findViewById<View>(R.id.cancel) as Button
        cancel!!.setOnClickListener { activity!!.onBackPressed() }

        return rootView

    }


    fun addChandas() {
        if (chanda_tj!!.text.isEmpty() && chanda_wj!!.text.isEmpty() && chanda_ef!!.text.isEmpty() && chanda_fitrana!!.text.isEmpty() && chanda_sadqa!!.text.isEmpty()) {
            Toast.makeText(activity!!.baseContext, "Enter Values", Toast.LENGTH_LONG).show()
        } else {

            val editit = sp!!.edit()

            tj = if (chanda_tj!!.text.isNotEmpty()) chanda_tj!!.text.toString().toInt() else 0
            wj = if (chanda_wj!!.text.isNotEmpty()) chanda_wj!!.text.toString().toInt() else 0
            ef = if (chanda_ef!!.text.isNotEmpty()) chanda_ef!!.text.toString().toInt() else 0
            fit = if (chanda_fitrana!!.text.isNotEmpty()) chanda_fitrana!!.text.toString().toInt() else 0
            sad = if (chanda_sadqa!!.text.isNotEmpty()) chanda_sadqa!!.text.toString().toInt() else 0

            editit.putInt("tj", tj)
            editit.putInt("wj", wj)
            editit.putInt("ef", ef)
            editit.putInt("fit", fit)
            editit.putInt("sad", sad)

            val otherTotal = sad + tj + wj + ef + fit

            Toast.makeText(activity!!.baseContext, otherTotal.toString(), Toast.LENGTH_LONG).show()

            editit.putInt("others", otherTotal)
            editit.apply()

            activity!!.onBackPressed()
        }

    }

    private fun retainAmount() {
        chanda_tj!!.setText(sp!!.getInt("tj", 0).toString(), TextView.BufferType.EDITABLE)
        chanda_wj!!.setText(sp!!.getInt("wj", 0).toString(), TextView.BufferType.EDITABLE)
        chanda_ef!!.setText(sp!!.getInt("ef", 0).toString(), TextView.BufferType.EDITABLE)
        chanda_fitrana!!.setText(sp!!.getInt("fit", 0).toString(), TextView.BufferType.EDITABLE)
        chanda_sadqa!!.setText(sp!!.getInt("sad", 0).toString(), TextView.BufferType.EDITABLE)
    }
}
