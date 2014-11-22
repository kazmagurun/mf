package com.kazma.mathfactor;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Subclasses SQLiteOpenHelper to manage database creation and version management.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	/** The name of episodes table. */
	public static final String EPISODES = "episodes";
	/** The name of the title column. */
	public static final String TITLE = "title";
	/** The name of the download column. */
	public static final String DOWNLOAD = "download";
	/** The name of the link column. */
	public static final String LINK = "link";
	/** The name of the position column. */
	public static final String POSITION = "position";
	/** The name of the total length column. */
	public static final String LENGTH = "length";
	
	/** The database file name. */
	private static final String FILENAME = "episodes.db";
	
	/** The database version. */
	private static final int VERSION = 1;
	
	private Context mContext;
	
	/**
	 * Creates an SQLiteHelper. Does not create/initialize the database yet.
	 * 
	 * @param context  the application context
	 */
	public SQLiteHelper(Context context) {
		super(context, FILENAME, null, VERSION);
		mContext = context;
	}
	
	/**
	 * Creates a new episodes database.
	 * 
	 * @param db  the SQLite database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + EPISODES 
					+ " (" + TITLE + " TEXT PRIMARY KEY, "
					+ DOWNLOAD + " TEXT, "
					+ LINK + " TEXT, "
					+ POSITION + " INTEGER, "
					+ LENGTH + " INTEGER " + ");");
		
		// fill the table with initial data
	    JsonFeeder feeder = new JsonFeeder("items.json", mContext.getAssets());
	    feeder.init();
	    List<Item> items = feeder.getItems();
	    String s = "INSERT INTO " + EPISODES + " (" + TITLE + "," + DOWNLOAD + "," + LINK + ") VALUES ";
	    for(Item item : items) {
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(s);
	    	sb.append("( \"");
	    	sb.append(item.getTitle());
	    	sb.append("\" , \"");
	    	sb.append(item.getDownload());
	    	sb.append("\" , \"");
	    	sb.append(item.getLink());
	    	sb.append("\" )");
	    	sb.append(";");
	    	db.execSQL(sb.toString());
	    }
	}	

	/**
	 * Called to upgrade database.
	 * 
	 * @oaram db  the SQLite database
	 * @param oldVersion  the old database version
	 * @param newVersion  the new database version 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
