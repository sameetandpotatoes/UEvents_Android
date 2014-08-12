package com.sapra.uevents;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatter {
	public static String shortDate(String inputDate){
		String input = inputDate;
		if (input == null)
			return "";
        DateFormat fromFormat = new SimpleDateFormat("EEEE, MMMM dd");
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("MM/dd/yy");
        toFormat.setLenient(false);
        try {
        	String output = toFormat.format(fromFormat.parse(input));
			return output;
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return "No Date";
	}
	public static String formatDate(String inputDate){
		String input = inputDate;
		if (input == null)
			return "";
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("EEEE, MMMM dd");
        toFormat.setLenient(false);
        try {
        	String output = toFormat.format(fromFormat.parse(input));
			return output;
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return "No Date";
	}
	public static String formatTime(String inputTime){
		String input = inputTime;
	    DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // don't even try to parse time zone
		fromFormat.setLenient(false);
		DateFormat toFormat = new SimpleDateFormat("h:mm a");
		toFormat.setLenient(false);
		try{
			String output = toFormat.format(fromFormat.parse(input));
			return output;
		} catch(ParseException pe){
			pe.printStackTrace();
		}
		return "No Time";
	}
}
