package com.onyx.android.sun.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by li on 2017/10/11.
 */

public class TimeUtils {
    private final static String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String DATA_FORMAT = "yyyy-MM-dd";
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DATA_TIME_FORMAT);
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(DATA_FORMAT);

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

    public static int compareDate(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat(DATA_FORMAT);
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

    public static boolean compareCurrentDate(String date) {
        if (date == null) {
            return false;
        }
        String currentDate = getCurrentDataInString();
        return compareDate(currentDate, date) > 0;
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
}
