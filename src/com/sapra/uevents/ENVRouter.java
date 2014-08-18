package com.sapra.uevents;

import android.os.Build;

public class ENVRouter {
	public static final String prodENV = "http://uevents.io/";
	public static final String localENV = "http://uevents.192.168.1.75.xip.io:20559/";
	
	public static final String myEventsURL = "api/v2/events/user.json";
	public static final String eventsURL = "api/v2/events.json";
	public static final String schoolsURL = "api/v2/schools.json";
	public static final String createUserURL = "api/v2/users/";
	public static final String updateUserURL = "api/v2/users/";
	public static final String postRSVPURL = "api/v2/events/";
	/**
     * Determines if local or production environment
     * @return Returns correct environment URL
     */
	public static String getENV(){
//		if (Build.FINGERPRINT.contains("generic")){
//			return localENV;
//		} else{
//			return prodENV;
//		}
		return localENV;
	}
	public static String postRSVPURL(String status, String event_id){
		return getENV() + postRSVPURL + event_id+"/"+status+"?authentication_token="+User.authToken;
	}
	public static String myEventsURL(){
		return getENV() + myEventsURL + "?authentication_token="+User.authToken;
	}
	public static String eventsURL(){
		return getENV() + eventsURL + "?authentication_token="+User.authToken;
	}
	public static String schoolsURL(){
		return getENV() + schoolsURL;
	}
	public static String createUserURL(){
		return getENV() + createUserURL;
	}
	public static String updateUserURL(){
		return getENV() + updateUserURL + User.id + "?authentication_token="+User.authToken;
	}
	public static int getFacebookAppId(){
		if (getENV().equals(localENV)){
			return R.string.dev_appId;
		} else{
			return R.string.prod_appId;
		}
	}
}
