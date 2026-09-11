package com.local.amjbc.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.local.amjbc.R
import com.local.amjbc.model.NavDrawerItem
import java.util.ArrayList

class NavDrawerListAdapter(private val context: Context, private val navDrawerItems: ArrayList<NavDrawerItem>?) : BaseAdapter() {

    override fun getCount(): Int {
        return navDrawerItems?.size ?: 0
    }

    override fun getItem(position: Int): Any? {
        return navDrawerItems?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val mInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = mInflater.inflate(R.layout.drawer_listview_item, null)
        }

        val imgIcon = view!!.findViewById<View>(R.id.icon) as ImageView
        val txtTitle = view.findViewById<View>(R.id.title) as TextView
        val txtCount = view.findViewById<View>(R.id.counter) as TextView

        val item = navDrawerItems!![position]
        imgIcon.setImageResource(item.icon)
        txtTitle.text = item.title

        // displaying count
        // check whether it set visible or not
        if (item.getCounterVisibility()) {
            Log.v("chk", "here here")
            txtCount.text = ""
        } else {
            // hide the counter view
            txtCount.visibility = View.GONE
        }

        return view
    }
}
