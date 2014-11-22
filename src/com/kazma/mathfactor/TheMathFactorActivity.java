package com.kazma.mathfactor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import android.widget.BaseAdapter;

/**
 * Shows the list of episodes and lets user manage the list.
 */
public class TheMathFactorActivity extends ListActivity {
	
	/** The log tag. */
	public final static String LOG_TAG = "MathFactor";
	
    /** 
     * Called when the activity is first created.  
     * 
     * @param savedInstanceState the saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Also read the extra info from database
        Database database = DatabaseFactory.getDatabase(this);
 
        final ItemAdapter itemAdapter = new ItemAdapter(this, database.getItems());
        // Bind to our new adapter.
        setListAdapter(itemAdapter);
        database.setObserver(new Database.DatabaseObserver() {
        	@Override
        	public void OnChanged(Item item, int position) {
        		// TODO(kazma) not working. we still cannot update the % completion correctly while
        		// the media is playing.
        		((BaseAdapter) itemAdapter).notifyDataSetChanged();
        	}
        });
    }
        
    /**
     * Action to take when a list item is clicked.
     * 
     * @param l the listview that contains the view that was clicked
     * @param w the view that was clicked within the list
     * @param position position of the view that was clicked
     * @param id the row id of the item that was clicked
     */
    @Override
    protected void onListItemClick(ListView l, View w, int position, long id) {
    	Log.v(LOG_TAG, "clicked on " + l.getItemAtPosition(position));
    	Item item = (Item)l.getItemAtPosition(position);
    	Intent intent = new Intent(this, PlayActivity.class);
    	intent.putExtra("title", item.getTitle());
    	intent.putExtra("download", item.getDownload());
    	intent.putExtra("link", item.getLink());
    	startActivity(intent);
    }
}