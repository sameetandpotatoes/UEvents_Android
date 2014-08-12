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
	private String description;
	private String attending;
	private String start_date, start_time;
	private String end_date, end_time;
	private String url;
	private String cover_url;
	private String location;
	private String owner;
	private String fbStatus;
	private String time;
	private String rawTime;
	public static final int CHAR_LENGTH = 50;
	private ArrayList<String> tags = new ArrayList<String>();
	public Event(JSONObject object){
		try{
			this.owner = object.getString("organizer");
			this.name = object.getString("title");
			int indexToCutOff = Math.min(name.length(), CHAR_LENGTH);
			this.name = name.substring(0, indexToCutOff);
			if (indexToCutOff == CHAR_LENGTH)
				name += "...";
			this.location = (object.getString("location") == "null")? "No Location" 
																	: (object.getString("location"));
			this.description = object.getString("description");
			this.start_date = DateFormatter.formatDate(object.getString("start_date"));
			this.start_time = DateFormatter.formatTime(object.getString("start_time"));
			this.end_date = DateFormatter.formatDate(object.getString("end_date"));
			this.end_time = DateFormatter.formatTime(object.getString("end_time"));
			this.attending = object.getString("attending");
			this.id = object.getString("event_id");
//			this.venue = object.getString("venue")
			JSONArray tags = object.getJSONArray("tags");
			for (int i = 0; i < tags.length(); i++){
				String tag = tags.get(i).toString();
				this.tags.add(tag);
			}
			this.cover_url = object.getString("primary_image_url");
			this.url = object.getString("url");
			fbStatus = "declined";
		}
		catch(JSONException e){
			
		}
	}
	public String getStart_time(){
		return start_time;
	}
	public String getRawTime(){
		return rawTime;
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
		// TODO Auto-generated method stub
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
    }
}
