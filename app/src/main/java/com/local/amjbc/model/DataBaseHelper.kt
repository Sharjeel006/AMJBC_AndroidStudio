package com.local.amjbc.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    private var mDataBase: SQLiteDatabase? = null
    private val mContext: Context

    init {
        DB_PATH = if (Build.VERSION.SDK_INT >= 17) {
            context.applicationInfo.dataDir + "/databases/"
        } else {
            "/data/data/" + context.packageName + "/databases/"
        }
        this.mContext = context
    }

    @Throws(IOException::class)
    fun createDataBase() {
        //If database not exists copy it from the assets 
        val mDataBaseExist = checkDataBase()
        if (!mDataBaseExist) {
            this.readableDatabase
            this.close()
            try {
                //Copy the database from assests 
                copyDataBase()
                Log.e(TAG, "createDatabase database created")
            } catch (mIOException: IOException) {
                throw Error("ErrorCopyingDataBase")
            }
        }
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name 
    private fun checkDataBase(): Boolean {
        val dbFile = File(DB_PATH + DB_NAME)
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists()); 
        return dbFile.exists()
    }

    //Copy the database from assets 
    @Throws(IOException::class)
    private fun copyDataBase() {
        val mInput: InputStream = mContext.assets.open(DB_NAME)
        val outFileName = DB_PATH + DB_NAME
        val mOutput: OutputStream = FileOutputStream(outFileName)
        val mBuffer = ByteArray(1024)
        var mLength: Int
        while (mInput.read(mBuffer).also { mLength = it } > 0) {
            mOutput.write(mBuffer, 0, mLength)
        }
        mOutput.flush()
        mOutput.close()
        mInput.close()
    }

    //Open the database, so we can query it 
    @Throws(SQLException::class)
    fun openDataBase(): Boolean {
        val mPath = DB_PATH + DB_NAME
        //Log.v("mPath", mPath); 
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY)
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS); 
        return mDataBase != null
    }

    @Synchronized
    override fun close() {
        if (mDataBase != null) mDataBase!!.close()
        super.close()
    }

    override fun onCreate(arg0: SQLiteDatabase) {
        // TODO Auto-generated method stub
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO Auto-generated method stub
    }

    fun insertQuery(s: String) {
        try {
            val db = this.writableDatabase
            db.rawQuery(s, null).close()
            db.close()
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
            throw mSQLException
        }
    }

    fun selectQuery() {
        Log.v("extracting data", "slect query")
        val sq = "SELECT * FROM Sync_Status"
        val db = this.writableDatabase

        val cursor = db.rawQuery(sq, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.v(cursor.getString(0), cursor.getString(1))
                Log.v(cursor.getString(2), cursor.getString(3))
                //Log.v("Max Sync_ID:",cursor.getString(0));
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun addMay(dated: String?, fajar: String?, zuhr: String?, asar: String?, maghrib: String?, isha: String?) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("Dated", dated)
        values.put("Fajar", fajar)
        values.put("Zuhr", zuhr)
        values.put("Asar", asar)
        values.put("Maghrib", maghrib)
        values.put("Isha", isha)

        // Inserting Row
        db.insert("May", null, values)
        db.close() // Closing database connection
    }

    fun addtimings(tid: String?, tnamaz: String?, ttime: String?) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("t_id", tid)
        values.put("t_namaz", tnamaz)
        values.put("t_time", ttime)


        // Inserting Row
        db.insert("timings", null, values)
        db.close() // Closing database connection
    }

    fun addInfo(iid: String?, iText: String?) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("i_id", iid)
        values.put("i_text", iText)


        // Inserting Row
        db.insert("Info", null, values)
        db.close() // Closing database connection
    }


    fun getInfo(iid: String) {
        Log.v("information id", iid)
        val sq = "SELECT * FROM  Info"
        val db = this.writableDatabase

        val cursor = db.rawQuery(sq, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.v("i_id:", Utility.GetColumnValue(cursor, "i_id"))
                Log.v("i_id:", Utility.GetColumnValue(cursor, "i_text"))
                //Log.v("product_id:",Utility.GetColumnValue(cursor, "product_id"));
                //Log.v(cursor.getString(0),cursor.getString(1));
                //Log.v(cursor.getString(2),cursor.getString(3));
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun getEvents(): String? {
        var s: String? = null
        //Log.v("extracting data","slect query");
        val sq = "SELECT * FROM events"
        val db = this.writableDatabase

        val cursor = db.rawQuery(sq, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //Log.v(cursor.getString(0),cursor.getString(1));
                //Log.v(cursor.getString(2),cursor.getString(3));
                s = cursor.getString(0)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return s
    }

    fun getMay(): String? {
        var s: String? = null

        val sq = "SELECT * FROM May"
        val db = this.writableDatabase

        val cursor = db.rawQuery(sq, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //Log.v(cursor.getString(0),cursor.getString(1));
                //Log.v(cursor.getString(2),cursor.getString(3));
                s = cursor.getString(0)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return s
    }


    fun getDataFromTable(tName: String) {
        Log.v("extracting data", "slect query")
        val sq = "SELECT * FROM $tName"
        Log.v("extracting data", sq)
        val db = this.writableDatabase

        val cursor = db.rawQuery(sq, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.v(cursor.getColumnName(0), cursor.getString(0))
                Log.v(cursor.getColumnName(1), cursor.getString(1))
                Log.v(cursor.getString(2), cursor.getString(2))
                Log.v(cursor.getString(3), cursor.getString(3))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }


    fun deleteRow(tName: String, colName: String, id: String) {
        val db = this.writableDatabase
        db.delete(tName, "$colName=$id", null)
        db.close()
    }

    companion object {
        private const val TAG = "DataBaseHelper" // Tag just for the LogCat window 

        //destination path (location) of our database on device 
        private var DB_PATH = ""
        private const val DB_NAME = "AMJBC" // Database name 
    }
}
