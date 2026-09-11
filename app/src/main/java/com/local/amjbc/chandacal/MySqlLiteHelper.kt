package com.local.amjbc.chandacal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MySqlLiteHelper(context: Context?) : SQLiteOpenHelper(context, db_name, null, db_version) {

    override fun onCreate(db: SQLiteDatabase) {
        if (!isTableExists(db, db_name)) db.execSQL(db_create)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + db_table)
        onCreate(db)
    }

    fun isTableExists(db: SQLiteDatabase, tableName: String?): Boolean {
        val cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + db_table + "'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.close()
                return true
            }
            cursor.close()
        }
        return false
    }

    companion object {
        private const val db_name = "Chandas"
        private const val db_version = 1

        const val db_table = "Record"

        const val db_columnId = "Id"
        const val db_columnMain = "Main"
        const val db_columnJSalana = "Jsalana"
        const val db_columnOrg = "Organizational"
        const val db_columnTj = "TehreekeJadeed"
        const val db_columnWj = "WaqfeJadeed"
        const val db_columnEf = "EidFund"
        const val db_columnFit = "Fitrana"
        const val db_columnSad = "Sadqa"
        const val db_dateTime = "DateTime"
        const val db_sync = "Synced"


        private const val db_create = ("create table " + db_table + " ( " + db_columnId + " integer primary key autoincrement, "
                + db_columnMain + " text , " + db_columnJSalana + " text, " + db_columnOrg + " text, " + db_columnTj
                + " text , " + db_columnWj + " text, " + db_columnEf + " text, " + db_columnFit + " text, "
                + db_columnSad + " text, " + db_dateTime + " text, " + db_sync + " text " + " )")
    }
}
