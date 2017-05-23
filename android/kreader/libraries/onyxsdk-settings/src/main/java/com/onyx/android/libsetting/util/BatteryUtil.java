package com.onyx.android.libsetting.util;

import android.content.Context;
import android.os.BatteryManager;

import com.onyx.android.libsetting.R;

/**
 * Created by solskjaer49 on 2017/5/23 15:08.
 */

public class BatteryUtil {
    public static final String EXTRA_USAGE_TIME = "extra_usage_time";
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    public static String getBatteryStatusByStatusCode(final Context context, final int status) {
        String statusStr = null;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusStr = context.getString(R.string.battery_info_status_unknown);
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusStr = context.getString(R.string.battery_info_status_charging);
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusStr = context.getString(R.string.battery_info_status_discharging);
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusStr = context.getString(R.string.battery_info_status_not_charging);
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusStr = context.getString(R.string.battery_info_status_full);
                break;
        }
        return context.getString(R.string.battery_status, statusStr);
    }

    public static String getVisualBatteryLevel(final Context context, final int level) {
        return context.getString(R.string.battery_percentage, level);
    }

    /**
     * Returns elapsed time for the given millis, in the following format:
     * 2d 5h 40m 29s
     *
     * @param context the application context
     * @param millis  the elapsed time in milli seconds
     * @return the formatted elapsed time
     */
    private static String formatElapsedTime(Context context, double millis, boolean inclSeconds) {
        StringBuilder sb = new StringBuilder();
        int seconds = (int) Math.floor(millis / 1000);
        if (!inclSeconds) {
            // Round up.
            seconds += 30;
        }

        int days = 0, hours = 0, minutes = 0;
        if (seconds > SECONDS_PER_DAY) {
            days = seconds / SECONDS_PER_DAY;
            seconds -= days * SECONDS_PER_DAY;
        }
        if (seconds > SECONDS_PER_HOUR) {
            hours = seconds / SECONDS_PER_HOUR;
            seconds -= hours * SECONDS_PER_HOUR;
        }
        if (seconds > SECONDS_PER_MINUTE) {
            minutes = seconds / SECONDS_PER_MINUTE;
            seconds -= minutes * SECONDS_PER_MINUTE;
        }
        if (inclSeconds) {
            if (days > 0) {
                sb.append(context.getString(R.string.battery_history_days,
                        days, hours, minutes, seconds));
            } else if (hours > 0) {
                sb.append(context.getString(R.string.battery_history_hours,
                        hours, minutes, seconds));
            } else if (minutes > 0) {
                sb.append(context.getString(R.string.battery_history_minutes, minutes, seconds));
            } else {
                sb.append(context.getString(R.string.battery_history_seconds, seconds));
            }
        } else {
            if (days > 0) {
                sb.append(context.getString(R.string.battery_history_days_no_seconds,
                        days, hours, minutes));
            } else if (hours > 0) {
                sb.append(context.getString(R.string.battery_history_hours_no_seconds,
                        hours, minutes));
            } else {
                sb.append(context.getString(R.string.battery_history_minutes_no_seconds, minutes));
            }
        }
        return sb.toString();
    }

    public static String getVisualBatteryUsageTime(final Context context, final long usageTime) {
        return context.getString(R.string.battery_stats_on_battery, formatElapsedTime(context, usageTime / 1000, true));
    }

}
