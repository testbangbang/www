package com.onyx.jdread.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huxiaomao on 2017/1/12.
 */

public class TimeUtils {
    public static final String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATA_FORMAT = "yyyy-MM-dd";
    public static final String DATA_HOUR_FORMAT = "yyyy-MM-dd_HH-mm";
    public static final String DATA_HOUR_FORMAT_CAST = "yyyyMMddHHmm";
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DATA_TIME_FORMAT);
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(DATA_FORMAT);
    public static final SimpleDateFormat DATA_HOUR_FORMAT_DATE = new SimpleDateFormat(DATA_HOUR_FORMAT);
    public static final SimpleDateFormat DATA_HOUR_FORMAT_DATE_CAST = new SimpleDateFormat(DATA_HOUR_FORMAT_CAST);

    private TimeUtils() {
        throw new AssertionError();
    }

    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    public static String getDate(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }

    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    public static String getCurrentDataInString() {
        return getDate(getCurrentTimeInLong());
    }

    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static Date parseDateDefault(String date) {
        try {
            return DEFAULT_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int compareDate(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat(DATA_TIME_FORMAT);
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static int daysBetween(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT);
        Calendar cal = Calendar.getInstance();
        long time1 = 0;
        long time2 = 0;

        try {
            cal.setTime(sdf.parse(date1));
            time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(date2));
            time2 = cal.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static String getFormatTime(String str) {
        String castString = "";
        try {
            Date date = DATA_HOUR_FORMAT_DATE.parse(str);
            castString = DATA_HOUR_FORMAT_DATE_CAST.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return castString;
    }

    public static int daysBetweenDefault(Date first, Date end) {
        try {
            first = DEFAULT_DATE_FORMAT.parse(DEFAULT_DATE_FORMAT.format(first));
            end = DEFAULT_DATE_FORMAT.parse(DEFAULT_DATE_FORMAT.format(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(first);
        long timeFirst = cal.getTimeInMillis();
        cal.setTime(end);
        long timeEnd = cal.getTimeInMillis();
        long between = (timeEnd - timeFirst) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between));
    }
}
