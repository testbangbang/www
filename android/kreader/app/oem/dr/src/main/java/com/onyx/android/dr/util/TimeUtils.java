package com.onyx.android.dr.util;

import android.support.annotation.NonNull;

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
    public static final String HOUR_AND_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String DATA_FORMAT = "yyyy-MM-dd";
    public static final String START_TIME = " 00:00:00";
    public static final String END_TIME = " 23:59:59";
    private static final int MILLISECOND = 1000;
    private static final int MINUTE_STEP = 60;
    private static final int MILLISECOND_STEP = 3600;
    private static final int HOUR_STEP = 24;
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

    public static boolean compareTime(String DATE1, String DATE2) {
        String date = getDate(getCurrentTimeMillis());
        DateFormat df = new SimpleDateFormat(HOUR_AND_MINUTE);
        try {
            Date dt1 = df.parse(date + " " + DATE1);
            Date dt2 = df.parse(date + " " + DATE2);
            if (dt1.getTime() < dt2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static long getStartDateMillisecond(String startDate) {
        DateFormat dateFormat = new SimpleDateFormat(DATA_TIME_FORMAT);
        try {
            Date date = dateFormat.parse(startDate + START_TIME);
            long time = date.getTime();
            return time;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static long getEndDateMillisecond(String endDate) {
        DateFormat dateFormat = new SimpleDateFormat(DATA_TIME_FORMAT);
        try {
            Date date = dateFormat.parse(endDate + END_TIME);
            long time = date.getTime();
            return time;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static String getDateString(int year, int month, int day) {
        String date;
        date = year + "-" + month + "-" + day;
        return date;
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
        long between_days = (time2 - time1) / (MILLISECOND * MILLISECOND_STEP * HOUR_STEP);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static Date string2date(String dateString, SimpleDateFormat format) {
        Date formateDate = null;
        try {
            formateDate = format.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return formateDate;
    }

    public static Date parseDate(String date) throws ParseException {
        return DATE_FORMAT_DATE.parse(date);
    }

    public static String formatDate(Date date) throws ParseException {
        return DATE_FORMAT_DATE.format(date);
    }

    public static Date parseDateDefault(String date) {
        try {
            return DEFAULT_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int daysBetween(Date smDate, Date bDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT);
        smDate = sdf.parse(sdf.format(smDate));
        bDate = sdf.parse(sdf.format(bDate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (MILLISECOND * MILLISECOND_STEP * HOUR_STEP);

        return Integer.parseInt(String.valueOf(between_days));
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
        long between = (timeEnd - timeFirst) / (MILLISECOND * MILLISECOND_STEP * HOUR_STEP);
        return Integer.parseInt(String.valueOf(between));
    }

    public static String getCurrentMonth(long timeMillis) {
        Calendar calender = getCalendar(timeMillis);
        int month = calender.get(Calendar.MONTH) + 1;
        return month + "";
    }

    @NonNull
    private static Calendar getCalendar(long timeMillis) {
        String dateString = getTime(timeMillis);
        Date date = null;
        try {
            date = DATE_FORMAT_DATE.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        return calender;
    }

    public static String getCurrentDay(long timeMillis) {
        Calendar calender = getCalendar(timeMillis);
        int day = calender.get(Calendar.DATE);
        return day + "";
    }

    public static int getCurrentHour() {
        Calendar calender = Calendar.getInstance();
        int hour = calender.get(Calendar.HOUR);
        return hour;
    }

    public static int getCurrentMinute() {
        Calendar calender = Calendar.getInstance();
        int minute = calender.get(Calendar.MINUTE);
        return minute;
    }

    public static long getCurrentTimeMillis() {
        long timeMillis = System.currentTimeMillis();
        return timeMillis;
    }

    public static String getWeekOfMonth(long timeMillis) {
        Calendar calender = getCalendar(timeMillis);
        int week = calender.get(Calendar.WEEK_OF_MONTH);
        return week + "";
    }
}
