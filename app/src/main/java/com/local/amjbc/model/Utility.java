package com.local.amjbc.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Utility {

 	public static String GetColumnValue(Cursor cur, String ColumnName) {
		try {
			int columnIndex = cur.getColumnIndex(ColumnName);
			if (columnIndex != -1) {
				return cur.getString(columnIndex);
			} else {
				// Handle gracefully – return null, empty string, or throw custom exception as needed
				return ""; // or `null`, depending on your logic
			}
		} catch (Exception ex) {
			Log.v("chk oo", ex.toString());
			return "";
		}
	}
 	
//	public static String GetString(EditText source) {
//		try {
//			return GetString(source.getText().toString());
//		} catch (Exception ex) {
//			return "";
//		}
//	}
//
//	public static String GetString(TextView source) {
//		try {
//			return GetString(source.getText().toString());
//		} catch (Exception ex) {
//			return "";
//		}
//	}
//
//	public static String GetString(Object source) {
//		try {
//			return GetString(source.toString());
//		} catch (Exception ex) {
//			return "";
//		}
//	}
	
	public static void ShowMessageBox(Context cont, String msg) {
		Toast toast = Toast.makeText(cont, msg, Toast.LENGTH_SHORT);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
		toast.show();
	}
}
