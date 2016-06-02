package com.local.amjbc.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by sharjeelhussain on 15-12-07.
 */
public class DataBaseAdapter {

    protected static final String TAG = "DB_ADAPTER_AMJBC";

    private final Context mContext;
    private SQLiteDatabase DB;
    private DataBaseHelper DBHelper;

    public DataBaseAdapter(Context context)
    {
        this.mContext = context;
        DBHelper = new DataBaseHelper(mContext);
    }

    public DataBaseAdapter createDatabase() throws SQLException
    {
        try
        {
            DBHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataBaseAdapter open() throws Exception
    {
        try
        {
            DBHelper.openDataBase();
            DBHelper.close();
            DB = DBHelper.getReadableDatabase();
        }
        catch (Exception mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        DBHelper.close();
    }


}
