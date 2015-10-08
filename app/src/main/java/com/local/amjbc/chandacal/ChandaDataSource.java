package com.local.amjbc.chandacal;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ChandaDataSource  {
	
	SQLiteDatabase database;
	MySqlLiteHelper dbhelper;
	SharedPreferences sp;
	Context mctx;
	
	public ChandaDataSource(Context context)
	{
		this.mctx = context;
		dbhelper = new MySqlLiteHelper(mctx);
	}
	
	public void open() throws SQLException 
	{
		database = dbhelper.getWritableDatabase();
	}
	
	public void close()
	{
		dbhelper.close();
	}
	
	public void insertChanda(String mainCh, String jSalana, String org ,int tj, int wj, int ef, int fit, int sad, String date, String syncVal)
	{
		ContentValues values = new ContentValues();
		
		String ff= mainCh;
		String qq = jSalana;
		String orgx = org;
		int tt = tj;
		int waj = wj;
		int efund = ef;
		int fitrana = fit;
		int sadqa = sad;
		
		values.put(MySqlLiteHelper.db_columnMain, ff);
		values.put(MySqlLiteHelper.db_columnJSalana, qq);
		values.put(MySqlLiteHelper.db_columnOrg, orgx );
		values.put(MySqlLiteHelper.db_columnTj, Integer.toString(tt));
		values.put(MySqlLiteHelper.db_columnWj, Integer.toString(waj));
		values.put(MySqlLiteHelper.db_columnEf, Integer.toString(efund));
		values.put(MySqlLiteHelper.db_columnFit, Integer.toString(fitrana));
		values.put(MySqlLiteHelper.db_columnSad, Integer.toString(sadqa));
		values.put(MySqlLiteHelper.db_dateTime, date);
		values.put(MySqlLiteHelper.db_sync, syncVal);
		
		database.insert(MySqlLiteHelper.db_table, null, values);
		
	}

}
