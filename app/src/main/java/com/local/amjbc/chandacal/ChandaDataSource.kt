package com.local.amjbc.chandacal

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class ChandaDataSource(context: Context) {
    var database: SQLiteDatabase? = null
    var dbhelper: MySqlLiteHelper = MySqlLiteHelper(context)
    var sp: SharedPreferences? = null
    var mctx: Context = context

    @Throws(SQLException::class)
    fun open() {
        database = dbhelper.writableDatabase
    }

    fun close() {
        dbhelper.close()
    }

    fun insertChanda(mainCh: String?, jSalana: String?, org: String?, tj: Int, wj: Int, ef: Int, fit: Int, sad: Int, date: String?, syncVal: String?) {
        val values = ContentValues()

        values.put(MySqlLiteHelper.db_columnMain, mainCh)
        values.put(MySqlLiteHelper.db_columnJSalana, jSalana)
        values.put(MySqlLiteHelper.db_columnOrg, org)
        values.put(MySqlLiteHelper.db_columnTj, tj.toString())
        values.put(MySqlLiteHelper.db_columnWj, wj.toString())
        values.put(MySqlLiteHelper.db_columnEf, ef.toString())
        values.put(MySqlLiteHelper.db_columnFit, fit.toString())
        values.put(MySqlLiteHelper.db_columnSad, sad.toString())
        values.put(MySqlLiteHelper.db_dateTime, date)
        values.put(MySqlLiteHelper.db_sync, syncVal)

        database!!.insert(MySqlLiteHelper.db_table, null, values)

    }

}
