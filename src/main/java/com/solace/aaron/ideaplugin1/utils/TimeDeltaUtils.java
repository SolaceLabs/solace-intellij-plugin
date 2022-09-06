package com.solace.aaron.ideaplugin1.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import com.solace.aaron.ideaplugin1.service.TokenHolder;

public class TimeDeltaUtils {

	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));

			
	/** Expects 2022-08-23T23:21:83.354Z */
	public static long parseTime(String time) {
		try {
			ZonedDateTime zdt = ZonedDateTime.parse(time, formatter);
			return zdt.toInstant().toEpochMilli();
		} catch (DateTimeParseException e) {
			return Long.MAX_VALUE;
		}
	}
	
	public static String formatTime(String time) {
		if (TokenHolder.props.getProperty("timeFormat").equalsIgnoreCase("timestamp")) {
			StringBuilder sb = new StringBuilder();
			sb.append(time.substring(0,10)).append(" ").append(time.substring(11, 19));
			return sb.toString();
		} else {
			return englishDifferenceFrom(time);
		}
	}

	
	
	public static String englishDifferenceFrom(String time) {
		long timestampMs = parseTime(time);
		if (timestampMs == Long.MAX_VALUE) {  // error handling
			return time;  // as is
		}
		return englishDelta(System.currentTimeMillis() - timestampMs);
	}
	
	public static String englishDifferenceFrom(long timestampMs) {
		long now = System.currentTimeMillis();
		return englishDelta(now - timestampMs);
	}

	public static String englishDelta(long deltaMs) {
		long deltaSeconds = deltaMs / 1000;
		StringBuilder sb = new StringBuilder();
		boolean negative = deltaMs < 0;
		if (negative) {
			deltaSeconds = Math.abs(deltaSeconds);  // flip it
		}
		if (deltaSeconds < 1) {
			sb.append("less than one second");
		} if (deltaSeconds < 2) {
		    sb.append("one second");
		} else if (deltaSeconds < 5) {
			sb.append("a few seconds");
		} else if (deltaSeconds < 60) {
			sb.append("about ").append(deltaSeconds).append(" seconds");
		} else if (deltaSeconds < 3600) {
			int val = (int)Math.round(deltaSeconds / 60.0);
			sb.append("about ").append(val).append(TopicUtils.pluralize(" minute", val));
		} else if (deltaSeconds < 86_400) {
			int deltaHalfHours = (int)((deltaSeconds + 900) / 1800);  // minus 15 minutes to shift from quarter-to to quarter-after
//			if (deltaHalfHours % 2 == 1) {
//				sb.append("about ").append(deltaHalfHours / 2).append("½ hours");
//			} else {
				sb.append("about ").append(deltaHalfHours / 2).append(TopicUtils.pluralize(" hour", deltaHalfHours / 2));
//			}
		} else if (deltaSeconds < 2_592_000) {  // about a month
			int deltaHalfDays = (int)((deltaSeconds + (3600 * 6)) / (3600 * 12));
//			if (deltaHalfDays % 2 == 1) {
//				sb.append("about ").append(deltaHalfDays / 2).append("½ days");
//			} else {
				sb.append("about ").append(deltaHalfDays / 2).append(TopicUtils.pluralize(" day", deltaHalfDays / 2));
//			}
		} else if (deltaSeconds < 31_557_600) {  // a year
			int deltaMonths = (int)(deltaSeconds / (3600 * 24 * 30.4375));  // rough approximation
			sb.append("about ").append(deltaMonths).append(TopicUtils.pluralize(" month", deltaMonths));
		} else {
			sb.append("more than a year");
		}
		if (negative) {
			sb.insert(0, "n the future by ");
		} else {
			sb.append(" ago");
		}
		return TopicUtils.capitalFirst(sb.toString());
	}
	
	
	
	public static void main(String... args) {
		
		int seconds = 1;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 3;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 12;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 35;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 59;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 65;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 569;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 600;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 610;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 3500;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = 3655;
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		System.out.println();
		seconds = (int)(3600 * 1.2);
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = (int)(3600 * 1.6);
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = (int)(3600 * 1.8);
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		System.out.println();
		seconds = (int)(86_400 * 0.9);
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = (int)(86_400 * 1);
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = (int)(86_400 * 1.2);
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		seconds = (int)(86_400 * 1.4);
		System.out.println(seconds + ": " + englishDelta(seconds * 1000));
		System.out.println();

		
		System.out.println(englishDifferenceFrom("2022-08-21T12:12:12.000Z"));
		System.out.println(parseTime("2022-08-21T12:12:12.000Z"));
		System.out.println(System.currentTimeMillis());
		System.out.println(englishDifferenceFrom("2022-08-26T12:12:12.000Z"));
		System.out.println(englishDifferenceFrom("2022-08-29T12:12:12.000Z"));
		
		
		
	}
	
}
