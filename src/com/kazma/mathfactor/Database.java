package com.kazma.mathfactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * Stores episode information to persistent storage using an SQLite database. It is expected that 
 * all methods will be called from the UI thread, however, The init() operation may not be quick, 
 * so it should be called as an async task. The caller should make sure no other methods called
 * until init() completes.
 */
public class Database {

	public static interface DatabaseObserver {
		void OnChanged(Item item, int position);
	}
	
	/** The table name. */
	private static final String EPISODES = SQLiteHelper.EPISODES;
	/** The name of the title column. */
	private static final String TITLE = SQLiteHelper.TITLE;
	/** The log tag. */
	private static final String LOG_TAG = TheMathFactorActivity.LOG_TAG;
	
	/** The SQLite helper. */
	private SQLiteHelper mHelper;
	/** The SQLite database. */
	private SQLiteDatabase mDatabase;
	/** The Database observer. */
	private DatabaseObserver mObserver;
	
	/**
	 * Creates a new Database object. This does not create or initialize an SQLite database.
	 * Completes quickly.  
	 * 
	 * @param context  the application context
	 */
	public Database(Context context) {
		mHelper = new SQLiteHelper(context);
	}
	
	/**
	 * Initializes a database. This operation may not be quick.
	 */
	public void init() {
		mDatabase = mHelper.getWritableDatabase();
	}
	
	public void setObserver(DatabaseObserver observer) {
		mObserver = observer;
	}
	
	public List<Item> getItems() {
		Cursor cursor = mDatabase.query(EPISODES, null, null, null, null, null, null);
		if (cursor == null) return null;
		ArrayList<Item> items = new ArrayList<Item>();
		int titleColumn = cursor.getColumnIndex(TITLE);
		int downloadColumn = cursor.getColumnIndex(SQLiteHelper.DOWNLOAD);
		int linkColumn = cursor.getColumnIndex(SQLiteHelper.LINK);
		int posColumn = cursor.getColumnIndex(SQLiteHelper.POSITION);
		int lengthColumn = cursor.getColumnIndex(SQLiteHelper.LENGTH);
		while (cursor.moveToNext()) {
			String title = cursor.getString(titleColumn);
			String download = cursor.getString(downloadColumn);
			String link = cursor.getString(linkColumn);
			int position = cursor.getInt(posColumn);
			int length = cursor.getInt(lengthColumn);
			items.add(new Item(title, link, download, position, length));
		}
		return items;
	}
	
	/**
	 * Closes a database.
	 */
	public void close() {
		mHelper.close();
	}
	
	/**
	 * Store episode position. 
	 * 
	 * @param item the episode item
	 * @param pos the position in milliseconds
	 */
	public void setPosition(Item item, int msec) {
		
		Cursor cursor = getEpisodeItem(item);
		ContentValues values = new ContentValues();
		if (cursor.getCount() > 0 ) {
			values.put(SQLiteHelper.POSITION, msec);
			mDatabase.update(EPISODES, values, TITLE + "=\"" + item.getTitle() +"\"", null);
		} else {
			values.put(TITLE, item.getTitle());
			values.put(SQLiteHelper.POSITION, msec);
			mDatabase.insert(EPISODES, null, values);
		}
		item.setPosition(msec);
		if (mObserver != null) {
			mObserver.OnChanged(item, msec);
		}
	}

	/**
	 * Get episode position. 
	 * 
	 * @param item the episode item
	 * @return the position in milliseconds
	 */
	public int getPosition(Item item) {
		
		int retval = 0;
		Cursor cursor = getEpisodeItem(item);
		if (cursor.moveToFirst()) {
			int pos = cursor.getColumnIndex(SQLiteHelper.POSITION);
			if (pos > 0) retval = cursor.getInt(pos);
		}
		return retval;
	}

	/**
	 * Finds the episode item in the database and returns it.
	 * 
	 * @param item the episode item
	 */
	private Cursor getEpisodeItem(Item item) {
		Cursor cursor = mDatabase.query(EPISODES, null, TITLE + "=\"" + item.getTitle() + "\"", null, null, null, null);
		if (cursor.getCount() > 1 ) {
			Log.w(LOG_TAG, "Unexpected multiple episode rows in database for title" + item.getTitle());
		}
		return cursor;
	}
	
	public void setLength(Item item, int msec) {
		Cursor cursor = getEpisodeItem(item);
		ContentValues values = new ContentValues();
		if (cursor.getCount() > 0 ) {
			values.put(SQLiteHelper.LENGTH, msec);
			mDatabase.update(EPISODES, values, TITLE + "=\"" + item.getTitle() +"\"", null);
		} else {
			values.put(TITLE, item.getTitle());
			values.put(SQLiteHelper.LENGTH, msec);
			mDatabase.insert(EPISODES, null, values);
		}
		item.setLength(msec);
		if (mObserver != null) {
			mObserver.OnChanged(item, msec);
		}
	}
}
