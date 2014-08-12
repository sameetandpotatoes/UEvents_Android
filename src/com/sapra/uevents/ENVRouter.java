package com.sapra.uevents;

import android.os.Build;

public class ENVRouter {
	public static final String prodENV = "http://uevents.io/";
//	public static final String localENV = "http://10.0.3.2/";
	public static final String localENV = "http://uevents.192.168.1.75.xip.io:20559/";
	
	public static final String myEventsURL = "api/events/user.json";
	public static final String eventsURL = "api/events.json";
	public static final String schoolsURL = "api/schools.json";
	public static final String createUserURL = "api/users/";
	public static final String updateUserURL = "api/users/";
	public static final String postRSVPURL = "api/events/";
	
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
}
