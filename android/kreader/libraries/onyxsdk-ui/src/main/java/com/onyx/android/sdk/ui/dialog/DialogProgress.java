package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;

/**
 * Created by ming on 2016/12/2.
 */

public class DialogProgress extends Dialog {

    private TextView titleView;
    private TextView subTitleView;
    private SeekBar progressBar;

    private int progress;
    private int maxValue;

    public DialogProgress(Context context, int progress, int maxValue) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_progress);
        this.progress = progress;
        this.maxValue = maxValue;
        initView();
    }

    private void initView() {
        titleView = (TextView) findViewById(R.id.title);
        subTitleView = (TextView) findViewById(R.id.sub_title);
        progressBar = (SeekBar) findViewById(R.id.progress);

        titleView.setVisibility(View.GONE);
        subTitleView.setVisibility(View.GONE);
        progressBar.setMax(maxValue);
        progressBar.setProgress(progress);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        progressBar.setProgress(progress);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        progressBar.setMax(maxValue);
    }

    public void setTitle(String title) {
        this.titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
    }

    public void setSubTitle(String subTitle) {
        this.subTitleView.setText(subTitle);
        subTitleView.setVisibility(View.VISIBLE);
    }

    public SeekBar getProgressBar() {
        return progressBar;
    }
}
