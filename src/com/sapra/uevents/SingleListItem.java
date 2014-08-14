package com.sapra.uevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.ImageLoader;
public class SingleListItem extends Activity{
    public static Session activeSession;
    public static String event_id;
	private SegmentedRadioGroup segmentText;
	private Event selectedEvent;
	private String userId;
	private Typeface regular, bold;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.uchicago))));
        setContentView(R.layout.single_list_item_view);
        regular = LoginUsingActivityActivity.regular;
        bold  = LoginUsingActivityActivity.bold;
        
        LinearLayout llTags = (LinearLayout) findViewById(R.id.tags);
        TextView txtDescription = (TextView) findViewById(R.id.lbl_desc);
        txtDescription.setTypeface(regular);
        TextView txtName = (TextView) findViewById(R.id.lbl_name);
        TextView txtStart = (TextView) findViewById(R.id.start);
        TextView txtLoc = (TextView) findViewById(R.id.location);
        TextView txtAttending = (TextView) findViewById(R.id.attending);
        TextView venue = (TextView) findViewById(R.id.venue);
        ((RadioButton) findViewById(R.id.going)).setTypeface(regular);
        ((RadioButton) findViewById(R.id.interested)).setTypeface(regular);
        ((RadioButton) findViewById(R.id.notgoing)).setTypeface(regular);
        
        Banner cover = (Banner) findViewById(R.id.cover_url);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
    	lparams.setMargins(30, 20, 30, 20);
        Intent i = getIntent();
        
        selectedEvent = (Event) i.getParcelableExtra("Event");
        activeSession = (Session) i.getExtras().getSerializable("Session");
        userId = User.id;
        
        String description = selectedEvent.getDescription();
        String location = selectedEvent.getLocation();
        String cover_url = selectedEvent.getCover_url();
        
        String name = selectedEvent.getName();
        String time = selectedEvent.getStart_time();
        String start = DateFormatter.shortDate(selectedEvent.getStart_date());
        String attending = selectedEvent.getAttending();
//        String venue = selectedEvent.getVenue();
        ArrayList<String> tags = selectedEvent.getTags();
        event_id = selectedEvent.getID();
        getRSVPStatus();
        txtName.setText(name);
        txtName.setTypeface(regular);
        txtName.setTextColor(getResources().getColor(R.color.white));
        txtDescription.setText(boldText("Details: \n\n",description));
        
        segmentText = (SegmentedRadioGroup) findViewById(R.id.segment_text);
        segmentText.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup group, final int checkedId) {
				if (checkedId == R.id.notgoing && selectedEvent.getStatus().equals("declined")
				||  checkedId == R.id.going && selectedEvent.getStatus().equals("attending")
				||  checkedId == R.id.interested && selectedEvent.getStatus().equals("maybe")){
					//Clicked the same one, so let's do nothing
				} else{
					String newStatus = "";
					switch(checkedId){
						case R.id.notgoing:
							newStatus = "declined"; break;
						case R.id.going:
							newStatus = "attending"; break;
						case R.id.interested:
							newStatus = "maybe"; break;
					}
					selectedEvent.setStatus(newStatus);
					if (postToAPI()){
		        		if (selectedEvent.getStatus().equals("declined")){
		        			Toast.makeText(getApplicationContext(), "Not going to " + selectedEvent.getName(), Toast.LENGTH_SHORT).show();
		        		}else if (selectedEvent.getStatus().equals("attending")){	
	        				Toast.makeText(getApplicationContext(), "Going to " + selectedEvent.getName(), Toast.LENGTH_SHORT).show();
	        			} else{
	        				Toast.makeText(getApplicationContext(), "Interested in " + selectedEvent.getName(), Toast.LENGTH_SHORT).show();
	        			}
		        		((RadioButton) findViewById(R.id.notgoing)).setBackgroundColor(getResources().getColor(R.color.white));
		        		((RadioButton) findViewById(R.id.notgoing)).setTextColor(getResources().getColor(R.color.uchicago));
						((RadioButton) findViewById(R.id.interested)).setBackgroundColor(getResources().getColor(R.color.white));
						((RadioButton) findViewById(R.id.interested)).setTextColor(getResources().getColor(R.color.uchicago));
						((RadioButton) findViewById(R.id.going)).setBackgroundColor(getResources().getColor(R.color.white));
						((RadioButton) findViewById(R.id.going)).setTextColor(getResources().getColor(R.color.uchicago));
						((RadioButton) findViewById(checkedId)).setBackgroundColor(getResources().getColor(R.color.uchicago));
						((RadioButton) findViewById(checkedId)).setTextColor(getResources().getColor(R.color.white));
					} else{
						Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
					}
				}
			}        	
        });
        if (start != null)
        {	
        	if (time == null)
        		txtStart.setText(start);
        	else
        		txtStart.setText(time);
        	txtStart.setTypeface(regular);
        }
        if (location != null)
        {	
        	txtLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.loc25, 0, 0, 0);
        	txtLoc.setText(location); 
        	txtLoc.setTypeface(regular);
        }
//        if (venue != null){
//        	venue.setText(text)
//        }
        txtAttending.setText(boldTextWithColor(attending, "\nattending"));
    	if (tags.size() == 1){
    		tags.add(0, "THISISAFILLER");
    	}
        for (String tag : tags)
        {
        	TextView tvTag = new TextView(this);
        	tvTag.setLayoutParams(lparams);
        	tvTag.setTextColor(Color.BLACK);
        	tvTag.setTextSize(17);
        	tvTag.setGravity(Gravity.CENTER);
        	if (tag.equals("THISISAFILLER")){
        		tvTag.setText("");
        	} else{
        		tvTag.setText(tag);
        		tvTag.setTypeface(regular);
        		tvTag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag25, 0, 0, 0);
        		tvTag.setCompoundDrawablePadding(15);
        		tvTag.setPadding(15, 5, 5, 5);
        	}
        	llTags.addView(tvTag, 0);
        }
        cover.setBackgroundResource(R.drawable.placeholder);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(Constants.config);
        imageLoader.displayImage(cover_url, cover);
    }
    private boolean postToAPI(){
        InputStream inputStream = null;
        String result = "";
        Log.i("API", "Post to API");
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(ENVRouter.postRSVPURL(selectedEvent.getStatus(), selectedEvent.getID()));

            String json = "";

            // 3. build jsonObject
	        JSONObject jsonObject = new JSONObject();

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content   
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Log.i("API", "Executed post request");
            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.i("API", "SL: " + httpResponse.getStatusLine());
            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null){
                result = convertInputStreamToString(inputStream);
                Log.i("API", result);
                return httpResponse.getStatusLine().getStatusCode() == 200;
            }else{
                result = "Did not work!";
                return false;
            }

        } catch (Exception e) {
        	Log.i("API", "Caught Exception");
        	e.printStackTrace();
        	return false;
        }
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }   
    private void getRSVPStatus() {
    	new Request(
    		    activeSession,
    		    "/"+selectedEvent.getID()+"/attending/"+userId,
    		    null,
    		    HttpMethod.GET,
    		    new Request.Callback() {
    		        public void onCompleted(Response response) {
    		        	if (response.getError() == null){
    		        		try {
								if(response.getGraphObject().getInnerJSONObject().getJSONArray("data").getJSONObject(0).get("rsvp_status").equals("attending")){
									selectedEvent.setStatus("attending");
									((RadioButton) findViewById(R.id.going)).setBackgroundColor(getResources().getColor(R.color.uchicago));
									((RadioButton) findViewById(R.id.going)).setTextColor(getResources().getColor(R.color.white));
								}
							} catch (JSONException e) {
								new Request(
						    		    activeSession,
						    		    "/"+selectedEvent.getID()+"/maybe/"+userId,
						    		    null,
						    		    HttpMethod.GET,
						    		    new Request.Callback() {
						    		        public void onCompleted(Response response) {
						    		        	if (response.getError() == null){
						    		        		try {
						    		        			if(response.getGraphObject().getInnerJSONObject().getJSONArray("data").getJSONObject(0).get("rsvp_status").equals("unsure")){
															selectedEvent.setStatus("maybe");
															((RadioButton) findViewById(R.id.interested)).setBackgroundColor(getResources().getColor(R.color.uchicago));
															((RadioButton) findViewById(R.id.interested)).setTextColor(getResources().getColor(R.color.white));
														}
						    		        			System.out.println("Status is now " + selectedEvent.getStatus());
													} catch (JSONException e) {
														new Request(
												    		    activeSession,
												    		    "/"+selectedEvent.getID()+"/declined/"+userId,
												    		    null,
												    		    HttpMethod.GET,
												    		    new Request.Callback() {
												    		        public void onCompleted(Response response) {
												    		        	if (response.getError() == null){
												    		        		try {
																				if(response.getGraphObject().getInnerJSONObject().getJSONArray("data").getJSONObject(0).get("rsvp_status").equals("declined")){
																					selectedEvent.setStatus("declined");
																					((RadioButton) findViewById(R.id.notgoing)).setBackgroundColor(getResources().getColor(R.color.uchicago));
																					((RadioButton) findViewById(R.id.notgoing)).setTextColor(getResources().getColor(R.color.white));
																				}
																			} catch (JSONException e) {
																				((RadioButton) findViewById(R.id.notgoing)).setBackgroundColor(getResources().getColor(R.color.uchicago));
																				((RadioButton) findViewById(R.id.notgoing)).setTextColor(getResources().getColor(R.color.white));
																			}
												    		        	}
												    		        	else{
												    		        		System.out.println(response.getError());
												    		        	}
												    		        }
												    		    }
												    		).executeAsync();
													}
						    		        	}
						    		        	else{
						    		        		System.out.println(response.getError());
						    		        	}
						    		        }
						    		    }
						    		).executeAsync();
							}
    		        	}
    		        	else{
    		        		System.out.println(response.getError());
    		        	}
    		        }
    		    }
    		).executeAsync();
	}
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case android.R.id.home:
	        	onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
	        	finish();
	            return true;
	        case R.id.menu_item_share:
	        	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	        	sharingIntent.setType("text/plain");
	        	String shareBody = "Hey, I'm interested in " + selectedEvent.getName() + " at " + selectedEvent.getLocation() +" ("+selectedEvent.getStart_time()+"). Want to join me?\n\nFind more events with UEvents, available on the App Store and the Play Store.\n\n\n\n\nhttp://www.uevents.io";
	        	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "UEvents Invitation to " + selectedEvent.getName());
	        	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	        	startActivity(Intent.createChooser(sharingIntent, "Share via"));
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	private Spannable boldText(String boldedText, String sourceString){
		Spannable spannable = new SpannableString(boldedText+sourceString);
    	spannable.setSpan(new CustomTypefaceSpan("bold", bold), 0, boldedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	spannable.setSpan(new CustomTypefaceSpan("regular", regular), boldedText.length(), boldedText.length() + sourceString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	return spannable;
	}
	private Spannable boldTextWithColor(String boldedText, String sourceString){
		Spannable spannable = new SpannableString(boldedText+sourceString);
    	spannable.setSpan(new CustomTypefaceSpan("bold", bold), 0, boldedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.uchicago)), 0, boldedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	spannable.setSpan(new CustomTypefaceSpan("regular", regular), boldedText.length(), boldedText.length() + sourceString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	return spannable;
	}
	@Override
    public void onStart(){
    	super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
    }
    @Override
    public void onStop(){
    	super.onStop();
    	EasyTracker.getInstance(this).activityStop(this);
    }
}