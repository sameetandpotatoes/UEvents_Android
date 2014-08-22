package com.sapra.uevents;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class CustomAdapter extends BaseAdapter {
    
    private ArrayList<Object> data;
    private LayoutInflater inflater=null;
    private ImageLoader imageLoader;
    
    public CustomAdapter(ArrayList<Object> listView){
      this.data = listView;
    }
    /**
     * @return size of ListView
     */
    public int getCount() {
        return data.size();
    }
    /**
     * Reads in config file and initializes application.
     *
     * @param position Position of item requested
     * @return Object at position 'position'
     */
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
    public int getViewTypeCount() {
        return 2;
    }
    public View getView(int position, View view, ViewGroup parent) {
    	ViewHolder vh = new ViewHolder();
    	vh.position = position;
    	Event tempEvent = null;
    	Context context = parent.getContext();
    	imageLoader = ImageLoader.getInstance();
    	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        imageLoader.init(config);
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = inflater.inflate(R.layout.list_item, parent, false);
        Typeface bold = Typeface.createFromAsset(context.getAssets(), Constants.BOLD);
    	TextView name = (TextView) view.findViewById(R.id.name);
    	TextView when = (TextView) view.findViewById(R.id.when);
    	TextView where = (TextView) view.findViewById(R.id.where);
        ImageView thumb_image= (ImageView) view.findViewById(R.id.list_image);
        name.setTypeface(bold);
        when.setTypeface(bold);
        where.setTypeface(bold);
        if (data.size() != 0 && position < data.size()){
	    	if (data.get(position) instanceof Event){
	    		tempEvent = (Event) data.get(position);
				name.setText(tempEvent.getName());
				when.setWidth(Constants.width/3);
	            when.setText(tempEvent.getStart_time());
	            where.setText(tempEvent.getLocation());
	            if (vh.position == position)
	            	imageLoader.displayImage(tempEvent.getCover_url(), thumb_image);
	    	}
	    	else if (data.get(position) instanceof TextView){
	    		RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.event);
	    		rl.setVisibility(View.GONE);
	    		TextView newDate = (TextView) view.findViewById(R.id.new_date);
	    		if (position != 0){
		    		newDate.setTypeface(bold);
		    		newDate.setText(((TextView) (data.get(position))).getText());
	    		} else{
	    			newDate.setVisibility(View.GONE);
	    		}
	    	}
	    	 //One last change to the UI for the last element
	        if (position == data.size() - 1){
	    		RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.event);
	    		rl.setPadding(0, 0, 0, 0);
	        }
        }
        vh = null;
        context = null;
        bold = null;
    	return view;
    }
}
class ViewHolder{
	int position;
}