package com.example.example6;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "lunchlist.db";
	private static final int SCHEMA_VERSION = 1;
	
	public RestaurantHelper(Context context) {
		super(context, DB_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE restaurants (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, type TEXT, notes TEXT);");		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	public Cursor getAll() {
		return (getReadableDatabase()
				.rawQuery("SELECT _id, name, address, type, notes FROM restaurants ORDER BY name", null));
	}
	
	public void insert(String name, String address, String type, String notes) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("address", address);
		cv.put("type", type);
		cv.put("notes", notes);
		getWritableDatabase().insert("restaurants", "name", cv);
	}
	
	public void update(Integer id, String name, String address, String type, String notes) {
		ContentValues cv = new ContentValues();
		cv.put("_id", id);
		cv.put("name", name);
		cv.put("address", address);
		cv.put("type", type);
		cv.put("notes", notes);
		String[] whereArgs = new String[]{id.toString()};
		getWritableDatabase().update("restaurants", cv, "_id=" + id , null);
	}
	
	public void delete(Integer id) {
		getWritableDatabase().delete("restaurants", "_id=" + id, null);
	}
		
	public Integer getId(Cursor c) {  return (c.getInt(0));  }
	public String getName(Cursor c) {  return (c.getString(1));  }
	public String getAddress(Cursor c) {  return (c.getString(2));  }
	public String getType(Cursor c) {  return (c.getString(3));  }
	public String getNotes(Cursor c) {  return (c.getString(4));  }
}
