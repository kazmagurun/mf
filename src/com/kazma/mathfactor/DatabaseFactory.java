package com.kazma.mathfactor;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Provides an already initialized database instance.
 */
public class DatabaseFactory {

	/** The storage instance. */
	private static Database _database;
	private static boolean _ready;
	/**
	 * Returns a storage instance.
	 * 
	 * @return a storage instance
	 * TODO: handle failure cases
	 */
	public static synchronized Database getDatabase(Context context) {
		if (!_ready) {
			_database = new Database(context);
			_database.init();
			_ready = true;
		}
		return _database;
	}	
}
