package com.local.amjbc.model

import android.content.Context
import android.database.Cursor
import android.util.Log
import android.view.Gravity
import android.widget.Toast

object Utility {

    @JvmStatic
    fun GetColumnValue(cur: Cursor, ColumnName: String?): String {
        return try {
            val columnIndex = cur.getColumnIndex(ColumnName)
            if (columnIndex != -1) {
                cur.getString(columnIndex)
            } else {
                // Handle gracefully – return null, empty string, or throw custom exception as needed
                "" // or `null`, depending on your logic
            }
        } catch (ex: Exception) {
            Log.v("chk oo", ex.toString())
            ""
        }
    }

    @JvmStatic
    fun ShowMessageBox(cont: Context?, msg: String?) {
        val toast = Toast.makeText(cont, msg, Toast.LENGTH_SHORT)
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setGravity(Gravity.CENTER or Gravity.CENTER, 0, 0)
        toast.show()
    }
}
