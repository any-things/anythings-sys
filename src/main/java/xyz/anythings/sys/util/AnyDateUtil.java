package xyz.anythings.sys.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import xyz.elidom.util.DateUtil;
import xyz.elidom.util.ValueUtil;

public class AnyDateUtil extends DateUtil{

	private static String DEFAULT_HOUR_FORMAT = "HH";
	private static String DEFAULT_MIN_FORMAT = "mm";

	/**
	 * date ==> String
	 * @param date
	 * @return
	 */
	public static String dateStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
		return sdf.format(date);		
	}
	
	/**
	 * currentDate + min
	 * @param addMinutes
	 * @return
	 */
	public static Date addMinutes(int addMinutes) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(new Date());
		c.add(Calendar.MINUTE, addMinutes);
		return c.getTime();
	}

	/**
	 * date + min 
	 * @param date
	 * @param addMinutes
	 * @return
	 */
	public static Date addMinutes(Date date, int addMinutes) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(date);
		c.add(Calendar.MINUTE, addMinutes);
		return c.getTime();
	}
	
	/**
	 * get hour string
	 * @param date
	 * @return
	 */
	public static String hourStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_HOUR_FORMAT);
		return sdf.format(date);		
	}
	
	/**
	 * get hour integer
	 * @param date
	 * @return
	 */
	public static int hourInt(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_HOUR_FORMAT);
		return ValueUtil.toInteger(sdf.format(date));		
	}
	
	/**
	 * get min integer
	 * @param date
	 * @return
	 */
	public static int minInt(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_MIN_FORMAT);
		return ValueUtil.toInteger(sdf.format(date));		
	}


	/**
	 * date set min
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date setMinutes(Date date, int minutes) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(date);
		c.set(Calendar.MINUTE,minutes);
		return c.getTime();
	}


	/**
	 * date set second
	 * @param date
	 * @param seconds
	 * @return
	 */
	public static Date setSeconds(Date date, int seconds) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(date);
		c.set(Calendar.SECOND,seconds);
		return c.getTime();
	}
}
