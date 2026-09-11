package com.local.amjbc.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.IOException
import java.sql.SQLException

/**
 * Created by sharjeelhussain on 15-12-07.
 */
class DataBaseAdapter(context: Context) {

    private val mContext: Context = context
    private var DB: SQLiteDatabase? = null
    private var DBHelper: DataBaseHelper = DataBaseHelper(mContext)

    @Throws(SQLException::class)
    fun createDatabase(): DataBaseAdapter {
        try {
            DBHelper.createDataBase()
        } catch (mIOException: IOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase")
            throw Error("UnableToCreateDatabase")
        }
        return this
    }

    @Throws(Exception::class)
    fun open(): DataBaseAdapter {
        try {
            DBHelper.openDataBase()
            DBHelper.close()
            DB = DBHelper.readableDatabase
        } catch (mSQLException: Exception) {
            Log.e(TAG, "open >>$mSQLException")
            throw mSQLException
        }
        return this
    }

    fun close() {
        DBHelper.close()
    }

    companion object {
        protected const val TAG = "DB_ADAPTER_AMJBC"
    }
}
