/**
 *
 */
package com.onyx.android.sdk.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.onyx.android.sdk.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author joy
 */
public class DateTimeUtil {
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMM = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_T_HHMMSS_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMMSS_FOR_FILE_NAME = new SimpleDateFormat("yyyy-MM-dd HH mm ss", Locale.getDefault());

    /**
     * format the time according to the current locale and the user's 12-/24-hour clock preference
     *
     * @param context
     * @return
     */
    public static String getCurrentTimeString(Context context) {
        return DateFormat.getTimeFormat(context).format(new Date());
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT_YYYYMMDD_HHMMSS.format(date);
    }

    public static String formatDate(Date date, SimpleDateFormat simpleDateFormat) {
        if (date == null) {
            return "";
        }
        return simpleDateFormat.format(date);
    }

    public static long parse(String dateString, SimpleDateFormat simpleDateFormat) {
        return parse(dateString, simpleDateFormat, null);
    }

    public static long parse(String dateString, SimpleDateFormat simpleDateFormat, String timeZone) {
        if (!StringUtils.isNullOrEmpty(timeZone)) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date == null ? 0 : date.getTime();
    }

    public static String formatTime(Context context, long allSecond) {
        long hour_value = allSecond / 3600;
        long minute_value = allSecond % 3600 / 60;
        long second_value = allSecond % 3600 % 60;
        String whitespace_symbol = " ";

        String hour_symbol = context.getResources().getString(R.string.hour_symbol);
        String minute_symbol = context.getResources().getString(R.string.minute_symbol);
        String second_symbol = context.getResources().getString(R.string.second_symbol);

        if (hour_value > 0) {
            return hour_value + hour_symbol + whitespace_symbol + minute_value + minute_symbol;
        } else if (minute_value > 0) {
            return minute_value + minute_symbol + whitespace_symbol + second_value + second_symbol;
        } else {
            return second_value + second_symbol;
        }
    }
}
