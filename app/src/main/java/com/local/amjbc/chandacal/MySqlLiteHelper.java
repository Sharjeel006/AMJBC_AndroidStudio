package com.local.amjbc.chandacal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Context;

public class MySqlLiteHelper extends SQLiteOpenHelper {

	private static final String db_name = "Chandas" ;
	private static final int db_version = 1;
	
	public static final String db_table = "Record" ;
	
	public static final String db_columnId = "Id" ;
	public static final String db_columnMain = "Main" ;
	public static final String db_columnJSalana = "Jsalana" ;
	public static final String db_columnOrg = "Organizational" ;
	public static final String db_columnTj = "TehreekeJadeed" ;
	public static final String db_columnWj = "WaqfeJadeed" ;
	public static final String db_columnEf = "EidFund" ;
	public static final String db_columnFit = "Fitrana" ;
	public static final String db_columnSad = "Sadqa" ;
	public static final String db_dateTime = "DateTime" ;
	public static final String db_sync = "Synced" ;
	
	
	private static final String db_create = "create table " + db_table + " ( " + db_columnId + " integer primary key autoincrement, "
			+ db_columnMain + " text , " + db_columnJSalana + " text, " + db_columnOrg + " text, " + db_columnTj
			+ " text , " + db_columnWj + " text, " + db_columnEf + " text, " + db_columnFit +  " text, " 
			+ db_columnSad + " text, " + db_dateTime + " text, " +  db_sync + " text " + " )";
	
	
	public MySqlLiteHelper(Context context) {
		super(context, db_name, null, db_version);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		if (!isTableExists(db, db_name)) db.execSQL(db_create);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + db_table);
	    onCreate(db);
	}
	
	public boolean isTableExists(SQLiteDatabase db, String tableName) {
	    Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + db_table + "'", null);
	    if(cursor != null) {
	        if(cursor.getCount()>0) {
	            return true;
	        }
	    }
	    return false;
	}
	
}
