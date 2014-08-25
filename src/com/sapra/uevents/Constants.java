package com.sapra.uevents;

public class Constants {
	public static final int FOOD = 			4;
	public static final int MUSIC = 		5;
	public static final int OFF_CAMPUS = 	6;
	public static final int SPORTS = 		7;
	public static final int NIGHTLIFE = 	8;
	public static final int PAGES_COUNT = 	9;
	public static final int PADDING_DATE = 	30;
	
	public static final String REGULAR = "fonts/HN-Thin.otf";
	public static final String BOLD = "fonts/HN-Light.otf";
	
	public static final String FOODTAG = "Food & Dining";
	public static final String MUSICTAG = "Music & Fine Arts";
	public static final String OFF_CAMPUSTAG = "Off-Campus";
	public static final String SPORTSTAG = "Sports";
	public static final String NIGHTLIFETAG = "Nightlife";
	
	public static int width;
	public static int height;
	
	public static String getTag(int i){
		switch(i){
		case FOOD: return FOODTAG;
		case MUSIC: return MUSICTAG;
		case OFF_CAMPUS: return OFF_CAMPUSTAG;
		case SPORTS: return SPORTSTAG;
		case NIGHTLIFE: return NIGHTLIFETAG;
		default: return "";
		}
	}
}
