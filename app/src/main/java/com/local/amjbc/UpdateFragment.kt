package com.local.amjbc

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class UpdateFragment : Fragment() {

    private var isHiddenField = false
    private var hasOptions = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_update, container, false)

        setHasOptionsMenu(true)
        return rootView

    }


    fun isFragmentHidden(): Boolean {
        return isHiddenField
    }


    override fun setHasOptionsMenu(hasOptions: Boolean) {
        this.hasOptions = hasOptions
        super.setHasOptionsMenu(hasOptions)
    }

    fun onFragmentShown() {
        isHiddenField = false
        if (hasOptions) super.setHasOptionsMenu(true)
    }


    fun onFragmentHidden() {
        isHiddenField = true
        super.setHasOptionsMenu(false)
    }


}
