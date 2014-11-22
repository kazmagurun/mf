package com.kazma.mathfactor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * A Feeder that uses a JSON file as a feed source.
 */
public class JsonFeeder {

	/** The log tag. */
	final static String LOG_TAG = TheMathFactorActivity.LOG_TAG;
	
	/** The path to the JSON file. */
	String _path;
	
	/** The list of items. */
	List<Item> _items = new ArrayList<Item>();
	
	/** The asset manager. */
	AssetManager _assetMgr;
	
	/**
	 * Creates a new JsonFeeder.
	 *
	 * @param path  path to the JSON file
	 * @param assetMgr  the asset manager
	 */
	JsonFeeder(String path, AssetManager assetMgr) {
		_path = path;
		_assetMgr = assetMgr;
	}
	
	public boolean init() {
		return refresh();
	}
	
	/**
	 * Refreshes the item list from the feed source. 
	 * 
	 * @return a RefreshResult object describing the status of refresh operation.
	 */
	private boolean refresh() {
		// read the file to a string
		ArrayList<Item> list = new ArrayList<Item>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(_assetMgr.open(_path)));
			String line;
			while ((line = reader.readLine()) != null) {
				JSONObject jobject = new JSONObject(line);
				Item item = new Item(jobject.getJSONArray("title").getString(0),
									 jobject.getJSONArray("link").getString(0),
									 jobject.getJSONArray("download").getString(0));
				list.add(item);
				Log.v(LOG_TAG, jobject.toString());
				
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			Log.e(LOG_TAG, "feed SRC_NOT_FOUND");
			return false;
		} catch (JSONException ex) {
			ex.printStackTrace();
			Log.e(LOG_TAG, "feed PARSING_ERROR");
			return false;
		}
		// successfully parsed
		_items = list;
		return true;
	}

	/**
	 * Returns all the episode items. The returned list is ordered in the way it is received from the source.
	 *  
	 * @return all the episode items.
	 */
	public List<Item> getItems() {
		return _items;
	}
}
