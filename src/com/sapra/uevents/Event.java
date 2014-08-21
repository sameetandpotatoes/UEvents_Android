package com.sapra.uevents;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
	private String id;
	private String name;
	private String rawName;
	private String description;
	private String attending;
	private String start_date, start_time, shortStartDate;
	private String end_date, end_time;
	private String url;
	private String cover_url;
	private String location;
	private String owner;
	private String fbStatus;
	private String time;
	private String rawStartTime, rawEndTime;
	public static final int CHAR_LENGTH = 40;
	private ArrayList<String> tags = new ArrayList<String>();
	public Event(JSONObject object){
		try{
			this.owner = object.getString("organizer");
			this.name = object.getString("title");
			this.rawName = name;
			int indexToCutOff = Math.min(name.length(), CHAR_LENGTH);
			this.name = name.substring(0, indexToCutOff);
			if (indexToCutOff == CHAR_LENGTH)
				name += "...";
			this.location = (object.getString("location") == "null")? "No Location" 
																	: (object.getString("location"));
			this.description = object.getString("description");
			
			this.start_date 	= DateFormatter.formatADate(object.getString("start_date"), "yyyy-MM-dd", "EEEE, MMMM dd");
			this.shortStartDate = DateFormatter.formatADate(object.getString("start_date"), "yyyy-MM-dd", "MM/dd/yy");
			this.start_time 	= DateFormatter.formatTime(object.getString("start_time"));
			this.end_date 		= DateFormatter.formatDate(object.getString("end_time"));
			this.end_time		= DateFormatter.formatTime(object.getString("end_time"));
			this.rawStartTime 	= object.getString("start_time");
			this.rawEndTime 	= object.getString("end_time");
			if (rawEndTime.equals("null"))
				rawEndTime = rawStartTime;
			this.attending = object.getString("attending");
			this.id = object.getString("event_id");
//			this.venue = object.getString("venue");
			JSONArray tags = object.getJSONArray("tags");
			for (int i = 0; i < tags.length(); i++){
				JSONObject tag = tags.getJSONObject(i);
				this.tags.add(tag.getString("name"));
			}
			this.cover_url = object.getString("primary_image_url");
			this.url = object.getString("url");
			fbStatus = "declined";
			
		}
		catch(JSONException e){
			
		}
	}
	public String getRawName(){
		return rawName;
	}
	public String getStart_time(){
		return start_time;
	}
	public String getEnd_time(){
		return end_time;
	}
	public String getRawStartTime(){
		return rawStartTime;
	}
	public String getRawEndTime(){
		return rawEndTime;
	}
	public String getID(){
		return id;
	}
	public String getCover_url(){
		return cover_url;
	}
	public String getURL(){
		return url;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getAttending() {
		return attending;
	}
	public String getStart_date() {
		return start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public String getLocation() {
		return location;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public String getOwner() {
		return owner;
	}
	public String getStatus(){
		return fbStatus;
	}
	public String getShortStartDate(){
		return shortStartDate;
	}
	public void setStatus(String status){
		this.fbStatus = status;
	}
	@Override
	public boolean equals(Object otherEvent){
		if (otherEvent == null || !(otherEvent instanceof Event))
			return false;
		return ((Event) otherEvent).getName().equals(this.getName());
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(attending);
		out.writeString(id);
		out.writeString(cover_url);
		out.writeString(name);
		out.writeString(location);
		out.writeString(start_date);
		out.writeString(owner);
		out.writeList(tags);
		out.writeString(description);
		out.writeString(fbStatus);
		out.writeString(time);
		out.writeString(url);
		out.writeString(start_time);
		out.writeString(end_time);
		out.writeString(shortStartDate);
		out.writeString(rawStartTime);
		out.writeString(rawEndTime);
		out.writeString(rawName);
	}
	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    private Event(Parcel in){
    	this.attending = in.readString();
    	this.id = in.readString();
    	this.cover_url = in.readString();
    	this.name = in.readString();
    	this.location = in.readString();
    	this.start_date = in.readString();
    	this.owner = in.readString();
    	this.tags = in.readArrayList(null);
    	this.description = in.readString();
    	this.fbStatus = in.readString();
    	this.time = in.readString();
    	this.url = in.readString();
    	this.start_time = in.readString();
    	this.end_time = in.readString();
    	this.shortStartDate = in.readString();
    	this.rawStartTime = in.readString();
    	this.rawEndTime = in.readString();
    	this.rawName = in.readString();
    }
}
