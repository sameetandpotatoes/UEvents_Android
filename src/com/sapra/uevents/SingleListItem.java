package com.sapra.uevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
public class SingleListItem extends Activity{
    private Session activeSession;
	private SegmentedRadioGroup segmentText;
	private Event selectedEvent;
	private String userId;
	private Typeface regular, bold;
	private boolean alreadyCreatedEvent;
	private TextView txtAttending;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.uchicago))));
        setContentView(R.layout.single_list_item_view);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        
        regular = Typeface.createFromAsset(getAssets(), Constants.REGULAR);
        bold = Typeface.createFromAsset(getAssets(), Constants.BOLD);
        alreadyCreatedEvent = false;
        
        LinearLayout llTags = (LinearLayout) findViewById(R.id.tags);
        TextView txtDescription = (TextView) findViewById(R.id.lbl_desc);
        txtDescription.setTypeface(regular);
        TextView txtName = (TextView) findViewById(R.id.lbl_name);
        TextView txtStart = (TextView) findViewById(R.id.start);
        TextView txtLoc = (TextView) findViewById(R.id.location);
        txtAttending = (TextView) findViewById(R.id.attending);
//        TextView venue = (TextView) findViewById(R.id.venue);
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
        String start = selectedEvent.getShortStartDate();
        String attending = selectedEvent.getAttending();
//      String venue = selectedEvent.getVenue();
        ArrayList<String> tags = selectedEvent.getTags();
        getRSVPStatus();
        txtName.setText(name);
        txtName.setTypeface(bold);
        txtName.setTextColor(getResources().getColor(R.color.white));
        txtDescription.setText(boldTextSize("Details: \n\n",description, 65));
        
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
						//Posted successfully
						//Let's tell the user
		        		if (selectedEvent.getStatus().equals("declined")){
		        			Toast.makeText(SingleListItem.this, "Not going to " + selectedEvent.getName(), Toast.LENGTH_SHORT).show();
		        		}else if (selectedEvent.getStatus().equals("attending")){	
	        				Toast.makeText(SingleListItem.this, "Going to " + selectedEvent.getName(), Toast.LENGTH_SHORT).show();
	        			} else{
	        				Toast.makeText(SingleListItem.this, "Interested in " + selectedEvent.getName(), Toast.LENGTH_SHORT).show();
	        			}
		        		((RadioButton) findViewById(R.id.notgoing)).setBackground(getResources().getDrawable(R.drawable.rightborder));
		        		((RadioButton) findViewById(R.id.notgoing)).setTextColor(getResources().getColor(R.color.uchicago));
						((RadioButton) findViewById(R.id.interested)).setBackground(getResources().getDrawable(R.drawable.rightborder));
						((RadioButton) findViewById(R.id.interested)).setTextColor(getResources().getColor(R.color.uchicago));
						((RadioButton) findViewById(R.id.going)).setBackground(getResources().getDrawable(R.drawable.rightborder));
						((RadioButton) findViewById(R.id.going)).setTextColor(getResources().getColor(R.color.uchicago));
						if (checkedId == R.id.notgoing){
							((RadioButton) findViewById(checkedId)).setBackground(getResources().getDrawable(R.drawable.leftborderactive));
						} else{
							((RadioButton) findViewById(checkedId)).setBackground(getResources().getDrawable(R.drawable.rightborderactive));
						}
						((RadioButton) findViewById(checkedId)).setBackgroundColor(getResources().getColor(R.color.uchicago));
						((RadioButton) findViewById(checkedId)).setTextColor(getResources().getColor(R.color.white));
					} else{
						Toast.makeText(SingleListItem.this, "Error", Toast.LENGTH_SHORT).show();
					}
				}
			}        	
        });
        if (start != null)
        {	
        	if (time == null)
        		txtStart.setText(start);
        	else
        		txtStart.setText(start + " " + time);
        	txtStart.setTypeface(bold);
        	txtStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock25, 0, 0, 0);
        	txtStart.setCompoundDrawablePadding(10);
        }
        if (location != null)
        {	
        	txtLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.loc25, 0, 0, 0);
        	txtLoc.setCompoundDrawablePadding(10);
        	txtLoc.setText(location); 
        	txtLoc.setTypeface(bold);
        }
//        if (venue != null){
//        	venue.setText(text)
//        }
        txtAttending.setText(boldTextWithColorSize(attending, "\nattending", 70));
        txtAttending.setWidth(Constants.width/3);
    	if (tags.size() == 0){
    		tags.add(0, "All Events");
    	}
        for (String tag : tags)
        {
        	TextView tvTag = new TextView(this);
        	tvTag.setLayoutParams(lparams);
        	tvTag.setTextColor(Color.BLACK);
        	tvTag.setTextSize(15);
        	tvTag.setGravity(Gravity.CENTER);
        	if (tag.equals("Music & Fine Arts")){
        		tvTag.setTextSize(13);
        	} else if (tag.equals("Food & Dining")){
        		tvTag.setTextSize(13);
        	}
    		tvTag.setText(tag);
    		tvTag.setTypeface(regular);
    		tvTag.setPadding(5,5,5,5);
    		tvTag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tags, 0, 0, 0);
    		tvTag.setCompoundDrawablePadding(5);
        	llTags.addView(tvTag, 0);
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
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
	        	onBackPressed();
	        	return true;
	        case R.id.menu_item_share:
	        	shareEvent();
	        	return true;
	        case R.id.cal26:
	        	if (!alreadyCreatedEvent){
		        	Intent intent = new Intent(Intent.ACTION_INSERT);  
		        	intent.setType("vnd.android.cursor.item/event");
		        	intent.setData(Events.CONTENT_URI);
		        	intent.putExtra(Events.TITLE, selectedEvent.getRawName());
		        	intent.putExtra(Events.DESCRIPTION, selectedEvent.getDescription());
		        	intent.putExtra("beginTime", DateFormatter.getTimeInMillis(selectedEvent.getRawStartTime()));
		        	intent.putExtra("endTime", DateFormatter.getTimeInMillis(selectedEvent.getRawEndTime()));
		        	intent.putExtra(Events.CALENDAR_ID, selectedEvent.getID());
		        	intent.putExtra(Events.ALL_DAY, false);
		        	if (!selectedEvent.getLocation().equals("No Location"))
		        		intent.putExtra(Events.EVENT_LOCATION, selectedEvent.getLocation());
		        	alreadyCreatedEvent = true;
		        	startActivity(intent);
	        	} else{
	        		Toast.makeText(this, "You already created a reminder for this event", Toast.LENGTH_SHORT).show();
	        	}
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
		Spannable boldSpan = boldText(boldedText, sourceString);
    	boldSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.uchicago)), 0, boldedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	return boldSpan;
	}
	private Spannable boldTextWithColorSize(String boldedText, String sourceString, int size){
		Spannable colorSpan = boldTextWithColor(boldedText, sourceString);
		colorSpan.setSpan(new TextAppearanceSpan(null, 0, size, null, null), 0, boldedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return colorSpan;
	}
	private Spannable boldTextSize(String boldedText, String sourceString, int size){
		Spannable boldSpan = boldText(boldedText, sourceString);
		boldSpan.setSpan(new TextAppearanceSpan(null, 0, size, null, null), 0, boldedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return boldSpan;
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
    @Override
    public void onResume(){
    	super.onResume();
    	setBounds();
    	txtAttending.setWidth(Constants.width/3);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
    private void shareEvent(){
        String shareBody = "Hey, I'm interested in " + selectedEvent.getRawName() + " at " + 
        					selectedEvent.getLocation() +" ("+selectedEvent.getStart_time()+"). "+
        					"Want to join me?\n\nFind more events with UEvents, available on the Play Store."+
        					"\n\n\n\n\nhttp://www.uevents.io";
        String shareSubject = "UEvents Invitation to " + selectedEvent.getName();
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        
        Intent sendIntent = new Intent(Intent.ACTION_SEND);     
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(emailIntent, "Share via");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();        
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if(packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if(packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if(packageName.contains("android.gm")) {
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);               
                    intent.setType("message/rfc822");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }
    public void setBounds() {
    	Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Constants.width = size.x;
        Constants.height = size.y;
	}
}