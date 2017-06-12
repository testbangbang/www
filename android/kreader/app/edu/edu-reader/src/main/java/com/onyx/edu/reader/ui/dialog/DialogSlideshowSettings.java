package com.onyx.edu.reader.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.onyx.edu.reader.R;
import com.onyx.android.sdk.ui.view.SeekBarWithEditTextView;
import com.onyx.edu.reader.device.DeviceConfig;


/**
 * Created by Solskjaer49 on 2014/4/15.
 */
public class DialogSlideshowSettings extends DialogBase {

    public static abstract class Callback {
        public abstract void done(int interval, int maxPageCount);
    }

    private Callback callback;

    private SeekBarWithEditTextView seekBarInterval;
    private SeekBarWithEditTextView seekBarMaxPageCount;
    private Button mCancelButton;
    private Button mConfirmButton;

    public DialogSlideshowSettings(Context context, Callback callback) {
        super(context);
        setCanceledOnTouchOutside(false);
        this.setContentView(R.layout.dialog_slideshow_settings);
        initView();
        initData();
        bindListener();

        this.callback = callback;
    }

    private void initData() {
        seekBarInterval.updateValue(R.string.dialog_slideshow_intervals_title,
                DeviceConfig.sharedInstance(getContext()).getDefaultSlideshowInterval(),
                DeviceConfig.sharedInstance(getContext()).getSlideshowMinimumInterval(),
                DeviceConfig.sharedInstance(getContext()).getSlideshowMaximumInterval());
        seekBarMaxPageCount.updateValue(R.string.dialog_slideshow_max_page_count_title,
                DeviceConfig.sharedInstance(getContext()).getDefaultSlideshowPages(),
                DeviceConfig.sharedInstance(getContext()).getSlideshowMinimumPages(),
                DeviceConfig.sharedInstance(getContext()).getSlideshowMaximumPages());
    }

    private void initView() {
        seekBarInterval = (SeekBarWithEditTextView) findViewById(R.id.seekbar_interval);
        seekBarMaxPageCount = (SeekBarWithEditTextView) findViewById(R.id.seekbar_max_page_count);
        mCancelButton = (Button) findViewById(R.id.button_Cancel);
        mConfirmButton = (Button) findViewById(R.id.button_Confirm);
    }

    private void bindListener() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarInterval.isCurrentValueValid() &&
                        seekBarMaxPageCount.isCurrentValueValid()) {
                    dismiss();
                    callback.done(seekBarInterval.getCurrentValue(),
                            seekBarMaxPageCount.getCurrentValue());
                }
            }
        });
    }

    public void show(int windowsHeight, int windowWidth, int positionX, int positionY) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        if (positionX != -1) {
            lp.x = positionX;
        }
        if (positionY != -1) {
            lp.y = positionY;
        }
        if (windowWidth >= 0) {
            lp.width = windowWidth;
        }
        if (windowsHeight >= 0) {
            lp.height = windowsHeight;
        }
        this.getWindow().setAttributes(lp);
        super.show();
    }
}
