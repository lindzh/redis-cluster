package com.linda.cluster.redis.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtils {

	private static ThreadLocal<SimpleDateFormat> chineseDayFormat = new ThreadLocal<SimpleDateFormat>();

	private static ThreadLocal<SimpleDateFormat> chineseMonthFormat = new ThreadLocal<SimpleDateFormat>();

	private static ThreadLocal<SimpleDateFormat> chineseTimeFormat = new ThreadLocal<SimpleDateFormat>();
	
	private static ThreadLocal<SimpleDateFormat> simpleMinuteFormat = new ThreadLocal<SimpleDateFormat>();

	private static ThreadLocal<SimpleDateFormat> simpleDayFormat = new ThreadLocal<SimpleDateFormat>();

	private static ThreadLocal<SimpleDateFormat> simpleMonthFormat = new ThreadLocal<SimpleDateFormat>();

	private static ThreadLocal<SimpleDateFormat> simpleYearFormat = new ThreadLocal<SimpleDateFormat>();
	
	private static long leastTime = 0;
	
	public static SimpleDateFormat getChineseDayFormatInstance(){
		SimpleDateFormat format = chineseDayFormat.get();
		if(format==null){
			chineseDayFormat.set(new SimpleDateFormat("yyyy年MM月dd日"));
			format = chineseDayFormat.get();
		}
		return format;
	}
	
	public static SimpleDateFormat getChineseMonthFormatInstance(){
		SimpleDateFormat format = chineseMonthFormat.get();
		if(format==null){
			chineseMonthFormat.set(new SimpleDateFormat("yyyy年MM月"));
			format = chineseMonthFormat.get();
		}
		return format;
	}

	public static SimpleDateFormat getChineseTimeFormatInstance(){
		SimpleDateFormat format = chineseTimeFormat.get();
		if(format==null){
			chineseTimeFormat.set( new SimpleDateFormat("yyyy年MM月dd日  HH:mm"));
			format = chineseTimeFormat.get();
		}
		return format;
	}
	
	public static SimpleDateFormat getSimpleDayFormatInstance(){
		SimpleDateFormat format = simpleDayFormat.get();
		if(format==null){
			simpleDayFormat.set( new SimpleDateFormat("yyyy-MM-dd"));
			format = simpleDayFormat.get();
		}
		return format;
	}
	
	public static SimpleDateFormat getSimpleMinuteFormatInstance(){
		SimpleDateFormat format = simpleMinuteFormat.get();
		if(format==null){
			simpleMinuteFormat.set( new SimpleDateFormat("yyyy-MM-dd.HH-mm"));
			format = simpleMinuteFormat.get();
		}
		return format;
	}
	
	public static SimpleDateFormat getSimpleMonthFormatInstance(){
		SimpleDateFormat format = simpleMonthFormat.get();
		if(format==null){
			simpleMonthFormat.set( new SimpleDateFormat("yyyy-MM"));
			format = simpleMonthFormat.get();
		}
		return format;
	}
	
	public static SimpleDateFormat getSimpleYearFormatInstance(){
		SimpleDateFormat format = simpleYearFormat.get();
		if(format==null){
			simpleYearFormat.set( new SimpleDateFormat("yyyy"));
			format = simpleYearFormat.get();
		}
		return format;
	}

	public static String toChineseDate(long time) {
		return getChineseTimeFormatInstance().format(new Date(time));
	}

	public static long toChineseTime(String time) {
		try {
			Date date = getChineseTimeFormatInstance().parse(time);
			return date.getTime();
		} catch (ParseException e) {
			throw new IllegalArgumentException("ERROR " + e.getMessage());
		}
	}

	public static String toChineseDay(long time) {
		return getChineseDayFormatInstance().format(new Date(time));
	}

	public static String toChineseMonth(long time) {
		return getChineseMonthFormatInstance().format(new Date(time));
	}

	public static int getMonthLength(long time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		return calendar.get(Calendar.DATE);
	}
	
	public static Calendar getCalendar(long time){
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		return calendar;
	}
	
	public static long getTime(long time,int hour,int minute,int sec){
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, sec);
		return calendar.getTimeInMillis();
	}

	public static long parseSimpleDate(String date) {
		try {
			Date parse = getSimpleDayFormatInstance().parse(date);
			return parse.getTime();
		} catch (ParseException e) {
			throw new IllegalArgumentException("ERROR " + e.getMessage());
		}
	}

	public static long getLeastTime() {
		try {
			if (leastTime == 0) {
				leastTime = getSimpleDayFormatInstance().parse("2000-01-01").getTime();
			}
			return leastTime;
		} catch (ParseException e) {
			throw new IllegalArgumentException("ERROR " + e.getMessage());
		}
	}

	public static long getDay(long time) {
		String format = getSimpleDayFormatInstance().format(new Date(time));
		try {
			return getSimpleDayFormatInstance().parse(format).getTime();
		} catch (ParseException e) {
			throw new IllegalArgumentException("ERROR " + e.getMessage());
		}
	}

	public static long getMonth(long time) {
		String format = getSimpleMonthFormatInstance().format(new Date(time));
		try {
			return getSimpleMonthFormatInstance().parse(format).getTime();
		} catch (ParseException e) {
			throw new IllegalArgumentException("ERROR " + e.getMessage());
		}
	}

	public static long getYear(long time) {
		String format = getSimpleYearFormatInstance().format(new Date(time));
		try {
			return getSimpleYearFormatInstance().parse(format).getTime();
		} catch (ParseException e) {
			throw new IllegalArgumentException("ERROR " + e.getMessage());
		}
	}

	public static String toSimpleTime(long time) {
		return getSimpleDayFormatInstance().format(new Date(time));
	}
	
	public static String toSimpleMinuteTime(long time) {
		return getSimpleMinuteFormatInstance().format(new Date(time));
	}

	public static long getCurrentTimeInSec() {
		long time = System.currentTimeMillis();
		return time / 1000;
	}

	public static long getWeek(long time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		Calendar newCalendar = new GregorianCalendar();
		newCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		newCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		newCalendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
		newCalendar.set(Calendar.DAY_OF_WEEK, 1);
		newCalendar.set(Calendar.HOUR, 0);
		newCalendar.set(Calendar.AM_PM, Calendar.AM);
		newCalendar.set(Calendar.MINUTE, 0);
		newCalendar.set(Calendar.SECOND, 0);
		newCalendar.set(Calendar.MILLISECOND, 0);
		return newCalendar.getTimeInMillis();
	}

	public class TimeUnit {

		public static final long MINUTE = 1000 * 60;

		public static final long HOUR = MINUTE * 60;

		public static final long DAY = HOUR * 24;

		public static final long MONTH = DAY * 30;

		public static final int MINUTE_IN_SEC = 60;

		public static final int HOUR_IN_SEC = 60 * 60;

		public static final int DAY_IN_SEC = HOUR_IN_SEC * 24;

		public static final int MONTH_IN_SEC = DAY_IN_SEC * 30;
	}

	public static String parseTimeToDayHourMinute(long time) {
		long day = time / TimeUnit.DAY;
		long hour = (time - day * TimeUnit.DAY) / TimeUnit.HOUR;
		long minute = (time - day * TimeUnit.DAY - hour * TimeUnit.HOUR) / TimeUnit.MINUTE;
		StringBuilder sb = new StringBuilder();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "时");
		}
		sb.append(minute + "分");
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(getDay(System.currentTimeMillis()));
		System.out.println(getDay(System.currentTimeMillis()-20000));
		System.out.println(getDay(System.currentTimeMillis()-14000));
		System.out.println(getDay(System.currentTimeMillis()-10000));
	}

}
