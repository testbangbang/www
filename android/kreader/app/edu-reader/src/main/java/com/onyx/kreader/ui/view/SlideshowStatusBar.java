package com.onyx.kreader.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.kreader.R;

/**
 * Created by joy on 12/27/16.
 */

public class SlideshowStatusBar extends LinearLayout {
    private TextView textViewPage;
    private TextView textViewBattery;
    private String page;
    private String battery;

    public SlideshowStatusBar(Context context, RelativeLayout parentLayout) {
        super(context);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_slideshow_status_bar, this, true);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        parentLayout.addView(this, p);

        textViewPage = (TextView)findViewById(R.id.textview_page);
        textViewBattery = (TextView)findViewById(R.id.textview_battery);
        page = context.getString(R.string.view_slideshow_status_bar_page);
        battery = context.getString(R.string.view_slideshow_status_bar_battery);
    }

    public void updateValue(int totalPage, int currentPage, int startBatteryPercentValue,
                            int currentBatteryPercentValue) {
        textViewPage.setText(String.format(page, currentPage, totalPage));
        textViewBattery.setText(String.format(battery, currentBatteryPercentValue, startBatteryPercentValue));
    }
}
