package com.sapra.uevents;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Session;

public class EventsFragment extends Fragment {
    private Context context;
    private TextView date;
    private ListView lv;
    private int currentVisibleItemCount;
	private int currentScrollState;
	private ArrayList<Event> allEvents;
    private ArrayList<TextView> allNewDates;
    protected ArrayList<Object> listView;
	private CustomAdapter adapter;
	private String url;
	private JSONArray event_groups;
	private Session session;
	private SwipeRefreshLayout swipeView;
	private String tag;
	public EventsFragment(){
		listView = new ArrayList<Object>();
		allNewDates = new ArrayList<TextView>();
        allEvents = new ArrayList<Event>();
	}
	public static final EventsFragment newInstance(String url, Session session, Context context, String tag){
		EventsFragment ef = new EventsFragment();
		String noAnd = url.replaceAll(" & ", "%20%26%20");
		String noSpaces = noAnd.replaceAll(" ", "%20");
		ef.url = noSpaces;
		ef.session = session;
		ef.context = context;
		ef.tag = tag;
        return ef;
	}
	private void setListeners(){
        lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectedEventListener(arg2);
			}
		});
        lv.setOnScrollListener(new OnScrollListener(){
			public void onScroll(AbsListView lw, final int firstVisibleItem,
	                 final int visibleItemCount, final int totalItemCount) {
				boolean swipeRefreshEnabled = false;
					try{
						Object possibleTextView = listView.get((Integer) lv.getItemAtPosition(firstVisibleItem));
						if (possibleTextView instanceof TextView){
							date.setText(((TextView) possibleTextView).getText());
						}
						if (possibleTextView instanceof Event){
							possibleTextView = (Event) possibleTextView;
							String startDate = ((Event) possibleTextView).getStart_date();
							if (!(date.getText().equals(startDate))){
								date.setText(startDate);
							}
						}
			            boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
			            boolean topOfFirstItemVisible = lv.getChildAt(0).getTop() == 0;
			            swipeRefreshEnabled = firstItemVisible && topOfFirstItemVisible;
					} catch(NullPointerException n){	
					} catch(IndexOutOfBoundsException i){
					}
					finally{
					    currentVisibleItemCount = visibleItemCount;
				        swipeView.setEnabled(swipeRefreshEnabled);
					}
			}
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
			    currentScrollState = scrollState;
			    this.isScrollCompleted();
			}
			private void isScrollCompleted() {
			    if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
			    	//Load more events
			    }
			}
		});
	}
	public void onDestroy(){
		super.onDestroy();
	}
	@Override
	public void onHiddenChanged(boolean hidden){
		if (!hidden && tag.equals("My Events")){
			getEvents();
		}
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	    if (isAdded()){
	    	((LoggedIn) getActivity()).setActionBarTitle("All Events", true);
	        View rootView = inflater.inflate(R.layout.activity, container, false);
	        Typeface bold  = LoggedIn.bold;
	        date = (TextView) rootView.findViewById(R.id.static_date);
	        date.setTypeface(bold);
	        lv = (ListView) rootView.findViewById(android.R.id.list);
	        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
	        swipeView.setColorScheme(
	        		R.color.uchicago_secondary,
	        		R.color.uchicago, 
	                R.color.uchicago_dark, 
	                R.color.getstarted_light);
	        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	            	getEvents();
	            }
	        });
	        getEvents();
	        bold = null;
	        return rootView;
	    } else {
	    	return null;
	    }
	}
	public void getEvents(){
		swipeView.setRefreshing(true);
        getActivity().runOnUiThread(new Runnable(){
        	public void run(){
        		new Handler().postDelayed(new Runnable(){
        			public void run(){
        				if (context != null){
        					new GetEvents(tag).execute();
    	        			if (adapter != null){
	    	        			adapter.notifyDataSetInvalidated();
	    	        			lv.invalidateViews();
    	        			}
    	        		}
        				swipeView.setRefreshing(false);
        			}
        		}, 1500);
        	}
        });
	}
	private void selectedEventListener(int position){
		Intent description = new Intent(context, SingleListItem.class);
		if (listView.get(position) instanceof Event){
			Event clickedEvent = (Event) listView.get(position);
			description.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			description.putExtra("Event", clickedEvent);
			description.putExtra("Session", session);
			startActivity(description);
		}
	}
	public class GetEvents extends AsyncTask<Void, Integer, Boolean>{
		@SuppressWarnings("unchecked")
		private String tag;
		public GetEvents(String tag){
			this.tag = tag;
		}
		protected Boolean doInBackground(Void... params){
			event_groups = new JSONParser().getNewJSONFromUrl(url, tag);
				listView.clear();
				allEvents.clear();
				allNewDates.clear();
				if (event_groups != null){
					for (int i = 0; i < event_groups.length(); i++){
						try{
							JSONObject event_group = (JSONObject) event_groups.get(i);
							JSONArray events = (JSONArray) event_group.get("events");
							if (events.length() > 0){
								TextView newDate = new TextView(context);
								String rawDate = (String) event_group.get("date");
								String formattedDate = DateFormatter.formatADate(rawDate, "yyyy-MM-dd", "EEEE, MMMM dd"); 
								newDate.setText(formattedDate);
								allNewDates.add(newDate);
								listView.add(newDate);
								for (int j = 0; j < events.length(); j++){
									Event event = new Event((JSONObject) events.get(j));
									allEvents.add(event);
									listView.add(event);
								}
							}
						} catch(JSONException e){
							e.printStackTrace();
						}
					}
			    	return true;
				}
			return false;
		}
		protected void onProgressUpdate(Integer...a){ }
		public void onPostExecute(Boolean success){
			if (success){
	    		adapter = new CustomAdapter(listView);
	    		lv.setAdapter(adapter);
	    		adapter.notifyDataSetChanged();
	    		setListeners();
    		} else{
    			//Error
    		}
    	}
	}
}
