/*
 * Copyright: Philipp Emmermacher 2020
 */
 
 package de.emmermacher.springboot.mongo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.bson.types.ObjectId;

public class MongoUtils {

  public static boolean isObjectId(String id) {
    try {
      new ObjectId(id);
      return true;
    } catch (Exception e) {
      return false;
    }
  }  
  
  public static boolean isValidDateString(String string, String format) {
    try {
      getSimpleDateFormat(format).parse(string);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  private static SimpleDateFormat getSimpleDateFormat(final String format, final String timezone) {
    ThreadLocal<SimpleDateFormat> formater = new ThreadLocal<SimpleDateFormat>() {
      protected SimpleDateFormat initialValue() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return simpleDateFormat;
      }
    };
    return formater.get();
  }
  
  private static SimpleDateFormat getSimpleDateFormat(final String format) {
    ThreadLocal<SimpleDateFormat> formater = new ThreadLocal<SimpleDateFormat>() {
      protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat(format, Locale.ENGLISH);
      }
    };
    return formater.get();
  }
  
  public static Date parseDate(String input, String format) throws ParseException {
    return getSimpleDateFormat(format).parse(input);
  }
  
  public static Date parseDate(String input, String format, String timezone) throws ParseException {
    return getSimpleDateFormat(format, timezone).parse(input);
  }
  
  public static String parseDate(Date input, String format) throws ParseException {
    return getSimpleDateFormat(format).format(input);
  }
  
  public static String parseDate(Date input, String format, String timezone) throws ParseException {
    return getSimpleDateFormat(format, timezone).format(input);
  }
}
