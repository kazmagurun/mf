package com.kazma.mathfactor;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<Item> {

	 public static class ViewHolder {
		 TextView name;
		 TextView extra;
	 }
	
	 public ItemAdapter(Context context, List<Item> items) {
	       super(context, R.layout.item, items);
	 }
	 
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
	       // Get the data item for this position
	       Item item = getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       ViewHolder viewHolder; // view lookup cache stored in tag
	       if (convertView == null) {
	    	   viewHolder = new ViewHolder();
	    	   convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
	    	   viewHolder.name = (TextView) convertView.findViewById(R.id.itemInfo);
	    	   viewHolder.extra = (TextView) convertView.findViewById(R.id.itemExtra);
	    	   convertView.setTag(viewHolder);
	       } else {
	    	   viewHolder = (ViewHolder) convertView.getTag();
	       }
	       // Populate the data into the template view using the data object
	       viewHolder.name.setText(item.getTitle());
	       viewHolder.extra.setText(item.getExtraInfo());
	       // Return the completed view to render on screen
	       return convertView;
	 }
}
