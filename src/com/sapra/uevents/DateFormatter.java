package com.sapra.uevents;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateFormatter {
	/**
     * Formats date to shortened date
     * @param inputDate Input Date
     * @return Returns shortened date
     */
	public static String shortDate(String inputDate){
		return formatADate(inputDate, "yyyy-MM-dd'T'HH:mm:ss", "MM/dd/yy");
	}
	/**
     * Formats date
     * @param inputDate Input Date
     * @return Returns formatted date
     */
	public static String formatDate(String inputDate){
		return formatADate(inputDate, "yyyy-MM-dd'T'HH:mm:ss", "EEEE, MMMM dd");
	}
	/**
     * Formats time
     * @param inputTime Input Time
     * @return Returns formatted time
     */
	public static String formatTime(String inputTime){
		return formatADate(inputTime, "yyyy-MM-dd'T'HH:mm:ss", "h:mm a");
	}
	public static String formatADate(String input, String fromFormat, String toFormat){
	    DateFormat from = new SimpleDateFormat(fromFormat);
		from.setLenient(false);
		DateFormat to = new SimpleDateFormat(toFormat);
		to.setLenient(false);
		try{
			String output = to.format(from.parse(input));
			return output;
		} catch(ParseException pe){
			pe.printStackTrace();
		}
		return "N/A";
	}
	public static long getTimeInMillis(String rawTime) {
		Calendar time = Calendar.getInstance();
		String year 	= formatADate(rawTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyy");
		String month 	= formatADate(rawTime, "yyyy-MM-dd'T'HH:mm:ss", "MM");
		String day 		= formatADate(rawTime, "yyyy-MM-dd'T'HH:mm:ss", "dd");
		String hour 	= formatADate(rawTime, "yyyy-MM-dd'T'HH:mm:ss", "HH");
		String minute 	= formatADate(rawTime, "yyyy-MM-dd'T'HH:mm:ss", "mm");
		//TODO extremely weird bug I gotta fix eventually, here is workaround
		month = Integer.toString(Integer.parseInt(month) - 1);
		time.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day),Integer.parseInt(hour),Integer.parseInt(minute));
		return time.getTimeInMillis();
	}
}
