package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.onyx.kreader.R;

import java.util.Calendar;

public class DialogSlideshowStatistic extends DialogBase
{
    public DialogSlideshowStatistic(Context context, Calendar startTime, Calendar endTime,
                                    int pageCount, int startBatteryPercent, int endBatteryPercent)
    {
        super(context, R.style.dialog_no_title);
        setContentView(R.layout.dialog_slideshow_statistic);
        setCanceledOnTouchOutside(false);

        setupLayout(startTime, endTime, pageCount, startBatteryPercent, endBatteryPercent);
    }

    private void setupLayout(Calendar startTime, Calendar endTime,
                             int pageCount, int startBatteryPercent, int endBatteryPercent) {
        ((TextView)findViewById(R.id.textview_duration_value)).setText(formatDuration(getContext(),
                ((endTime.getTimeInMillis() - startTime.getTimeInMillis()) / 1000)));
        ((TextView)findViewById(R.id.textview_page_count_value)).setText(String.valueOf(pageCount));
        ((TextView)findViewById(R.id.textview_start_battery_value)).setText(String.valueOf(startBatteryPercent) + "%");
        ((TextView)findViewById(R.id.textview_end_battery_value)).setText(String.valueOf(endBatteryPercent) + "%");

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private static String formatDuration(Context context, long seconds) {
        long h = seconds / (60 * 60);
        long remainder = seconds % (60 * 60);
        long m = remainder / 60;
        long s = remainder % 60;
        String strHour = h <= 0 ? "" : String.valueOf(h) + context.getString(R.string.hour_symbol);
        String strMinute = m <= 0 ? "" : String.valueOf(m) + context.getString(R.string.minute_symbol);
        String strSecond = s <= 0 ? "" : String.valueOf(s) + context.getString(R.string.second_symbol);
        return strHour + strMinute + strSecond;
    }

}