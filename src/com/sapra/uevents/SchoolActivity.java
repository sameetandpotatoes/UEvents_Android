package com.sapra.uevents;

import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;

public class SchoolActivity extends Activity{
	private SchoolAdapter schoolAdapter;
	private ListView listView;
	private Map<String, String>  allUniv;
	private Context context;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.uchicago))));
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        
        SpannableString s = new SpannableString("Choose Your School");
        s.setSpan(new TypefaceSpan("HN-Light.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        actionBar.setTitle(s);
        
        setContentView(R.layout.school);
        listView = (ListView) this.findViewById(android.R.id.list);
        allUniv = new TreeMap<String, String>();
        
        new GetSchoolEvents().execute();
        context = getApplicationContext();
        schoolAdapter = new SchoolAdapter(context, allUniv);
        listView.setAdapter(schoolAdapter);
        schoolAdapter.notifyDataSetChanged();
	}
	public void sendBackToAPI(String schoolName, String schoolId){
		User.schoolId = schoolId;
		User.schoolName = schoolName;
		if (postToAPI(schoolId)){
			tutorialPages();
		}
	}
	public boolean postToAPI(String schoolId){
		 Log.i("API", "Post to API");
		 try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            String url = ENVRouter.updateUserURL();
            HttpPut httpPost = new HttpPut(url);
            Log.i("API", url);
            String json = "";

            // 3. build jsonObject
	        JSONObject jsonObject = new JSONObject();
	        Log.i("API", "Attaching " + schoolId);
	        jsonObject.accumulate("school_id", schoolId);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content   
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Log.i("API", "Executed put request");
            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.i("API", "SL: " + httpResponse.getStatusLine());
            return httpResponse.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
        	Log.i("API", "Caught Exception");
        	e.printStackTrace();
        	return false;
        }
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
    public void tutorialPages(){
    	Intent tutorial = new Intent(getApplicationContext(), TutorialActivity.class);
    	onTrimMemory(TRIM_MEMORY_RUNNING_CRITICAL);
    	startActivity(tutorial);
    	finish();
    }
	class GetSchoolEvents extends AsyncTask<Void, Void, Boolean>{
		protected Boolean doInBackground(Void... params){
			JSONArray schools = new JSONParser().getSchools();
			if (schools != null){
				try{
					for (int i = 0 ; i < schools.length(); i++){
						JSONObject school = schools.getJSONObject(i);
						allUniv.put(school.getString("name"), school.getString("id"));
					}
					return true;
				} catch(JSONException e){
					e.printStackTrace();
				}
			}
			return false;
		}
		@SuppressWarnings("deprecation")
		public void onPostExecute(Boolean success){
			if (success){
				//Add option for school not listed
				schoolAdapter = new SchoolAdapter(context, allUniv);
		        listView.setAdapter(schoolAdapter);
		        schoolAdapter.notifyDataSetChanged();
			}  else{
	   			 AlertDialog ad = new AlertDialog.Builder(context).create();
	     	    ad.setCancelable(false);
	     	    ad.setTitle("Error Retrieving Schools");
	     	    ad.setMessage("Check your wireless connections and try again.");
	     	    ad.setButton("Try Again", new DialogInterface.OnClickListener() {
	     	        public void onClick(DialogInterface dialog, int which) {
	     	        	finish();
	     	        	startActivity(getIntent());
	     	        }
	     	    });
		     	ad.show();
			}
		}
	}
}
