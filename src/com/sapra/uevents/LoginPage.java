package com.sapra.uevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;

public class LoginPage extends FragmentActivity {
//   public static String userId = "", universityName= "", userName= "", userEmail= "", firstName= "", lastName= "", accessToken= "", pictureURL= "";
//   public static String auth_token, schoolId;
   public Context context;
   private TestFragmentAdapter mAdapter;
   private ViewPager mPager;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.login);
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        context = this;
    }
    public void loggedIn(){
    	Intent events = new Intent(getApplicationContext(), LoginUsingActivityActivity.class);
    	onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    	events.putExtra("Session", Session.getActiveSession());
//    	events.putExtra("User ID", userId);
//    	events.putExtra("School", universityName);
//    	events.putExtra("Username", userName);
//    	User.auth_token = auth_token;
    	startActivity(events);
    	finish();
    }
    public boolean postToAPI(){
        InputStream inputStream = null;
        String result = "", school_id = "";
        Log.i("API", "Post to API");
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(ENVRouter.createUserURL());

            String json = "";

            // 3. build jsonObject
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.accumulate("access_token",User.accessToken);
	        jsonObject.accumulate("first_name",User.firstName);
	        jsonObject.accumulate("last_name",User.lastName);
	        jsonObject.accumulate("id",User.id);
	        jsonObject.accumulate("email",User.email);
	        jsonObject.accumulate("picture_url",User.pictureURL);
	        jsonObject.accumulate("school_id", User.schoolId);

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
                JSONObject obj = new JSONObject(result);
                User.authToken = ((JSONObject) obj.get("user")).getString("authentication_token");
                school_id =((JSONObject) obj.get("user")).getString("school_id");
                Log.i("API", result);
                System.out.println(school_id.equals("null"));
                System.out.println(school_id == null);
                if (school_id.equals("null"))
                	return false;
                else{
                	User.schoolId = school_id;
                	Log.i("API", school_id);
                	return true;
                }
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
    public void onDestroy(){
    	super.onDestroy();
    }
	public void university() {
		Intent university = new Intent(getApplicationContext(), SchoolActivity.class);
    	onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
//    	university.putExtra("userId", userId);
//    	university.putExtra("Username", userName);
//    	university.putExtra("auth_token", auth_token);
    	startActivity(university);
    	finish();
	}
	public boolean getUserInfo(){
		Session activeSession = Session.getActiveSession();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        Request request = new Request(activeSession, "me");
        Response response = request.executeAndWait();
        if (response.getError() == null) {
           GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
           if (User.id != "")
        	   return false;
           User.id = graphUser.getId();
           User.name = graphUser.getName();
           User.email = (String) graphUser.getProperty("email");
           User.accessToken = Session.getActiveSession().getAccessToken();
           User.firstName = graphUser.getFirstName();
           User.lastName = graphUser.getLastName();
           User.pictureURL = "http://graph.facebook.com/"+User.id+"/picture?type=square";
        }
        return true;
	}
}
