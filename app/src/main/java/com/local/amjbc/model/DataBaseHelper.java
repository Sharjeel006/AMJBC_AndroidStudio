package com.local.amjbc.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBaseHelper extends SQLiteOpenHelper 
{ 
private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window 
//destination path (location) of our database on device 
private static String DB_PATH = "";  
private static String DB_NAME ="AMJBC";// Database name 
private SQLiteDatabase mDataBase;  
private final Context mContext; 
 
public DataBaseHelper(Context context)  
{ 
	super(context, DB_NAME, null, 1);// 1? its Database Version
    if(android.os.Build.VERSION.SDK_INT >= 4.2){
       DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
    }
    else
    {
       DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
    }
    this.mContext = context;
}    
 
public void createDataBase() throws IOException 
{ 
    //If database not exists copy it from the assets 
 
    boolean mDataBaseExist = checkDataBase(); 
    if(!mDataBaseExist) 
    { 
        this.getReadableDatabase(); 
        this.close(); 
        try  
        { 
            //Copy the database from assests 
            copyDataBase(); 
            Log.e(TAG, "createDatabase database created"); 
        }  
        catch (IOException mIOException)  
        { 
            throw new Error("ErrorCopyingDataBase"); 
        } 
    } 
} 
    //Check that the database exists here: /data/data/your package/databases/Da Name 
    private boolean checkDataBase() 
    { 
        File dbFile = new File(DB_PATH + DB_NAME); 
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists()); 
        return dbFile.exists(); 
    } 
 
    //Copy the database from assets 
    private void copyDataBase() throws IOException 
    { 
        InputStream mInput = mContext.getAssets().open(DB_NAME); 
        String outFileName = DB_PATH + DB_NAME; 
        OutputStream mOutput = new FileOutputStream(outFileName); 
        byte[] mBuffer = new byte[1024]; 
        int mLength; 
        while ((mLength = mInput.read(mBuffer))>0) 
        { 
            mOutput.write(mBuffer, 0, mLength); 
        } 
        mOutput.flush(); 
        mOutput.close(); 
        mInput.close(); 
    } 
 
    //Open the database, so we can query it 
    public boolean openDataBase() throws SQLException 
    { 
        String mPath = DB_PATH + DB_NAME; 
        //Log.v("mPath", mPath); 
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY); 
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS); 
        return mDataBase != null; 
    } 
 
    @Override 
    public synchronized void close()  
    { 
        if(mDataBase != null) 
            mDataBase.close(); 
        super.close(); 
    }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	} 
	
	
	  public void insertQuery(String s)
	    {
	    	try
	    	{
		    	SQLiteDatabase db = this.getWritableDatabase();
		    	db.rawQuery(s, null);
		    	db.close();
		    } 
	    	catch (SQLException mSQLException)  
	    	{ 
	    		Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
	    		throw mSQLException; 
	    	} 
	    }
	  
	    public void selectQuery()
	    {
	    	Log.v("extracting data","slect query");
	    	String sq="SELECT * FROM Sync_Status";
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	
	    	Cursor cursor = db.rawQuery(sq, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					
					Log.v(cursor.getString(0),cursor.getString(1));
					Log.v(cursor.getString(2),cursor.getString(3));
					//Log.v("Max Sync_ID:",cursor.getString(0));
					
				} while (cursor.moveToNext());
			}
			
			db.close();
	    }
	    
	    public void addMay(String dated,String fajar,String zuhr, String asar, String maghrib, String isha) {
	    	
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put("Dated",dated);
			values.put("Fajar", fajar); 
			values.put("Zuhr", zuhr);
			values.put("Asar", asar);
			values.put("Maghrib", maghrib);
			values.put("Isha", isha);
			
			// Inserting Row
			db.insert("May", null, values);
			db.close(); // Closing database connection
		}
	    
	    public void addtimings(String tid,String tnamaz,String ttime) {
			
	    	SQLiteDatabase db = this.getWritableDatabase();
	
			ContentValues values = new ContentValues();
			values.put("t_id",tid);
			values.put("t_namaz", tnamaz); 
			values.put("t_time", ttime);
			
			
			// Inserting Row
			db.insert("timings", null, values);
			db.close(); // Closing database connection
		}
	    
	    public void addInfo(String iid,String iText) {
	    	
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put("i_id",iid);
			values.put("i_text",iText);
			
			
			// Inserting Row
			db.insert("Info", null, values);
			db.close(); // Closing database connection
		}
	   
	    
	    public void getInfo(String iid)
	    {
	    	Log.v("information id",iid);
	    	String sq="SELECT * FROM  Info";
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	
	    	Cursor cursor = db.rawQuery(sq, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Log.v("i_id:",Utility.GetColumnValue(cursor, "i_id"));
					Log.v("i_id:",Utility.GetColumnValue(cursor, "i_text"));
					//Log.v("product_id:",Utility.GetColumnValue(cursor, "product_id"));
					//Log.v(cursor.getString(0),cursor.getString(1));
					//Log.v(cursor.getString(2),cursor.getString(3));
				} while (cursor.moveToNext());
			}
			
			db.close();
	    }
	    
	    public String getEvents()
	    {
	    	String s=null;
	    	//Log.v("extracting data","slect query");
	    	String sq = "SELECT * FROM events";
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	
	    	Cursor cursor = db.rawQuery(sq, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					//Log.v(cursor.getString(0),cursor.getString(1));
					//Log.v(cursor.getString(2),cursor.getString(3));
					s=cursor.getString(0);
					
				} while (cursor.moveToNext());
			}
			
			db.close();
			return s;
	    }
	    
	    public String getMay()
	    {
	    	String s=null;
	    	
	    	String sq = "SELECT * FROM May";
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	
	    	Cursor cursor = db.rawQuery(sq, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					//Log.v(cursor.getString(0),cursor.getString(1));
					//Log.v(cursor.getString(2),cursor.getString(3));
					s=cursor.getString(0);
					
				} while (cursor.moveToNext());
			}
			
			db.close();
			return s;
	    }
	    
	    
	    public void getDataFromTable(String tName)
	    {
	    	Log.v("extracting data","slect query");
	    	String sq="SELECT * FROM "+tName;
	    	Log.v("extracting data",sq);
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	
	    	Cursor cursor = db.rawQuery(sq, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Log.v(cursor.getColumnName(0),cursor.getString(0));
					Log.v(cursor.getColumnName(1),cursor.getString(1));
					Log.v(cursor.getColumnName(2),cursor.getString(2));
					Log.v(cursor.getColumnName(3),cursor.getString(3));
				} while (cursor.moveToNext());
			}
			
			db.close();
	    }
	   
	    	    
	    
	    public void deleteRow(String tName, String colName, String id)
	    {
	    	SQLiteDatabase db = this.getWritableDatabase();	
	    	db.delete(tName, colName+"="+id, null);
	    			db.close();
	    }
	    
 
} 

 
