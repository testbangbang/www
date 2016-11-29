package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.onyx.kreader.R;
import com.onyx.kreader.utils.DateTimeUtil;

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
        ((TextView)findViewById(R.id.textview_duration_value)).setText(DateTimeUtil.formatDuration(getContext(),
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

}