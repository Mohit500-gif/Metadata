package com.digi.uniprr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	private DateUtil() {

	}

	public static String getFormattedDate(Date date, SimpleDateFormat dateFormat) {
		String formattedDate = "";
		if (date != null) {
			formattedDate = dateFormat.format(date);
		}

		return formattedDate;
	}

	public static String addHoursToStringDate(String dateStr) {
		String newDateStr;
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));

		try {
			Date date = sdf.parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			calendar.add(Calendar.HOUR_OF_DAY, 5);
			calendar.add(Calendar.MINUTE, 30);

			Date newDate = calendar.getTime();
			newDateStr = sdf.format(newDate);
		} catch (ParseException e) {
			newDateStr = "";
			e.printStackTrace();
		}

		return newDateStr;
	}

	public static Date addHours(Date date) {
		Date newDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, 11);
		newDate = calendar.getTime();

		return newDate;

	}

}