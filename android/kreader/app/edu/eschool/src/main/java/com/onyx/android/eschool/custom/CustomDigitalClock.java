package com.onyx.android.eschool.custom;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.DigitalClock;

import java.util.Calendar;

/**
 * Created by suicheng on 2016/11/16.
 */
public class CustomDigitalClock extends DigitalClock {
    private Calendar calendar;
    private final static String hourMode12 = "hh:mm aa";//hh:mm aa
    private final static String hourMode24 = "kk:mm";//k:mm
    private FormatChangeObserver formatChangeObserver;

    private Runnable ticker;
    private Handler handler;

    private boolean tickerStopped = false;
    private boolean isHourMode24 = true;

    private String format;

    public CustomDigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public CustomDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        Resources r = context.getResources();
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        formatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, formatChangeObserver);

        setFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        tickerStopped = false;
        super.onAttachedToWindow();
        handler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        ticker = new Runnable() {
            public void run() {
                if (tickerStopped) return;
                calendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(format, calendar));
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                handler.postAtTime(ticker, next);
            }
        };
        ticker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        tickerStopped = true;
    }

    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        if (isHourMode24) {
            return true;
        }
        return DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
        if (get24HourMode()) {
            format = hourMode24;
        } else {
            format = hourMode12;
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }
}
