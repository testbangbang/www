package com.onyx.android.sdk.ui.dialog;


import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.view.BesselCurveView;
import com.onyx.android.sdk.ui.view.SeekBarWithEditTextView;

/**
 * Created by ming on 2016/11/24.
 */

public class DialogCustomLineWidth extends OnyxBaseDialog {

    public interface Callback {
        void done(int lineWidth);
    }

    private Button btnCancel;
    private Button btnOk;
    private BesselCurveView besselCurveView;
    private SeekBarWithEditTextView seekBarView;
    private int currentLineWidth;
    private int maxLineWidth;
    private int color;

    private Callback callback;

    public DialogCustomLineWidth(Context context, final int currentLineWidth, final int maxLineWidth, final int color, final Callback callback) {
        super(context, R.style.CustomDialog);
        this.currentLineWidth = currentLineWidth;
        this.color = color;
        this.maxLineWidth = maxLineWidth;
        this.callback = callback;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_line_width);
        initView();
    }

    private void initView() {
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);
        besselCurveView = (BesselCurveView) findViewById(R.id.bessel_view);
        seekBarView = (SeekBarWithEditTextView) findViewById(R.id.seek_bar_view);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.done(currentLineWidth);
                }
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        initSeerProgress();
        besselCurveView.setSize(currentLineWidth);

    }

    private void initSeerProgress() {
        seekBarView.updateValue(R.string.current_value, currentLineWidth, 1, maxLineWidth);
        seekBarView.setCallback(new SeekBarWithEditTextView.Callback() {
            @Override
            public void valueChange(int newValue) {
                currentLineWidth = newValue;
                besselCurveView.setSize(currentLineWidth);
            }
        });
    }
}
