package com.onyx.android.dr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.sdk.utils.StringUtils;


/**
 * Created by zhouzhiming on 2017/9/7.
 */
public class AlertInfoDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    private final boolean tag;
    private TextView titleView;
    private Button buttonOk;
    private Button buttonCancel;
    private CharSequence title;
    private String leftButton;
    private String rightButton;
    private OnOKClickListener oKClickListener;
    private OnCancelClickListener cancelClickListener;
    private float number = 0.6f;
    private final int seekBarMaxValue = 15;
    private TextView minValue;
    private TextView settingValue;
    private TextView maxValue;
    private SeekBar seekBar;
    private int seekBarValue;
    private LinearLayout dialogCenter;

    public interface OnOKClickListener {
        void onOKClick(int value);
    }

    public interface OnCancelClickListener {
        void onCancelClick();
    }

    public AlertInfoDialog(final Context context, final String title, final boolean tag, final String leftButton, final String rightButton) {
        super(context, R.style.dialog);
        this.context = context;
        this.title = title;
        this.tag = tag;
        this.leftButton = leftButton;
        this.rightButton = rightButton;
    }

    private void initView() {
        setContentView(R.layout.dialog_amend_user_backgroud);
        titleView = (TextView) findViewById(R.id.dialog_title);
        buttonOk = (Button) findViewById(R.id.dialog_button_confirm);
        buttonCancel = (Button) findViewById(R.id.dialog_button_cancel);
        minValue = (TextView) findViewById(R.id.dialog_seek_bar_min_value);
        settingValue = (TextView) findViewById(R.id.dialog_seek_bar_setting_value);
        maxValue = (TextView) findViewById(R.id.dialog_seek_bar_max_value);
        seekBar = (SeekBar) findViewById(R.id.dialog_seek_bar);
        dialogCenter = (LinearLayout) findViewById(R.id.dialog_center);
        initData();
    }

    private void initData() {
        this.titleView.setText(this.title);
        seekBar.setMax(seekBarMaxValue);
        settingValue.setText(context.getResources().getString(R.string.dialog_minute_setting_value) + 0);
        if (StringUtils.isNullOrEmpty(leftButton)) {
            buttonOk.setText(leftButton);
        }
        if (StringUtils.isNullOrEmpty(rightButton)) {
            buttonCancel.setText(rightButton);
        }
        if (tag) {
            dialogCenter.setVisibility(View.GONE);
        } else {
            dialogCenter.setVisibility(View.VISIBLE);
        }
        initEvent();
    }

    private void initEvent() {
        buttonCancel.setOnClickListener(this);
        buttonOk.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue = progress;
                settingValue.setText(context.getResources().getString(R.string.dialog_minute_setting_value) + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(final View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.dialog_button_confirm:
                confirmListener();
                break;

            case R.id.dialog_button_cancel:
                cancelListener();
                break;
            default:
                break;
        }
    }

    private void cancelListener() {
        cancel();
        if (this.cancelClickListener != null) {
            this.cancelClickListener.onCancelClick();
        }
    }

    private void confirmListener() {
        if (this.oKClickListener != null && !tag) {
            if (seekBarValue == 0) {
                CommonNotices.showMessage(context, context.getString(R.string.set_speech_time));
                return;
            }
        }
        cancel();
        this.oKClickListener.onOKClick(seekBarValue);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBlurEffect();
        initView();
    }

    protected void setBlurEffect() {
        final Window window = getWindow();
        final WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = number;
        window.setAttributes(layoutParams);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void setOKOnClickListener(final OnOKClickListener okClickListener) {
        this.oKClickListener = okClickListener;
    }

    public void setCancelOnClickListener(final OnCancelClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    @Override
    public void setTitle(final CharSequence title) {
        this.title = title;
        this.titleView.setText(this.title);
    }

    @Override
    public void setTitle(final int resId) {
        this.title = this.context.getResources().getText(resId);
        this.titleView.setText(this.title);
    }
}