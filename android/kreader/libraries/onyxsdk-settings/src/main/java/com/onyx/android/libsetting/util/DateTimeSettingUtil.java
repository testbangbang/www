package com.onyx.android.libsetting.util;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by solskjaer49 on 2016/12/6 18:32.
 */

public class DateTimeSettingUtil {
    private final static String AUTO_TIME_KEY = CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1) ?
            Settings.Global.AUTO_TIME : Settings.System.AUTO_TIME;
    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";

    public static void setAutoTimeEnabled(Context context, boolean enable) {
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            Settings.Global.putInt(context.getContentResolver(), AUTO_TIME_KEY,
                    enable ? 1 : 0);
        } else {
            Settings.System.putInt(context.getContentResolver(), AUTO_TIME_KEY,
                    enable ? 1 : 0);
        }
    }

    public static boolean isAutoTimeEnabled(Context context) {
        try {
            return CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1) ?
                    Settings.Global.getInt(context.getContentResolver(), AUTO_TIME_KEY) > 0
                    : Settings.System.getInt(context.getContentResolver(), AUTO_TIME_KEY) > 0;
        } catch (Settings.SettingNotFoundException snfe) {
            return false;
        }
    }

    public static boolean changeSystemTime(Context context, long targetTime) {
        try {
            AlarmManager am = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            am.setTime(targetTime);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeSystemTimeZone(Context context, String key_id) {
        try {
            AlarmManager am = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            am.setTimeZone(key_id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void set24HourEnabled(Context context, boolean enabled) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24,
                enabled ? HOURS_24 : HOURS_12);
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        context.sendBroadcast(timeChanged);
    }

    public static boolean is24HourEnabled(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    public static String getTimeZoneText(TimeZone tz) {
        boolean daylight = tz.inDaylightTime(new Date());
        return String.valueOf(formatOffset(tz.getRawOffset() +
                (daylight ? tz.getDSTSavings() : 0))) +
                ", " + tz.getDisplayName(daylight, TimeZone.LONG);
    }

    private static char[] formatOffset(int off) {
        off = off / 1000 / 60;

        char[] buf = new char[9];
        buf[0] = 'G';
        buf[1] = 'M';
        buf[2] = 'T';

        if (off < 0) {
            buf[3] = '-';
            off = -off;
        } else {
            buf[3] = '+';
        }

        int hours = off / 60;
        int minutes = off % 60;

        buf[4] = (char) ('0' + hours / 10);
        buf[5] = (char) ('0' + hours % 10);

        buf[6] = ':';

        buf[7] = (char) ('0' + minutes / 10);
        buf[8] = (char) ('0' + minutes % 10);

        return buf;
    }
}

