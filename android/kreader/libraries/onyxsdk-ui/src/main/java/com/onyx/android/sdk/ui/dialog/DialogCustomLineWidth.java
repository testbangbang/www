package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.view.BesselCurveView;
import com.onyx.android.sdk.ui.view.CircleView;
import com.onyx.android.sdk.utils.DimenUtils;


/**
 * Created by ming on 2016/11/24.
 */

public class DialogCustomLineWidth extends Dialog {

    public interface Callback {
        void done(int lineWidth);
    }

    private ImageView imageViewMinusButton;
    private SeekBar seekBarValueControl;
    private ImageView imageViewAddButton;
    private Button btnCancel;
    private Button btnOk;
    private LinearLayout previewLayout;
    private CircleView circleView;
    private BesselCurveView besselCurveView;
    private TextView startText;
    private TextView endText;

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
        imageViewMinusButton = (ImageView) findViewById(R.id.imageView_MinusButton);
        seekBarValueControl = (SeekBar) findViewById(R.id.seekBar_valueControl);
        imageViewAddButton = (ImageView) findViewById(R.id.imageView_AddButton);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);
        besselCurveView = (BesselCurveView) findViewById(R.id.bessel_view);
        circleView = (CircleView) findViewById(R.id.circle_view);
        previewLayout = (LinearLayout) findViewById(R.id.preview_layout);
        startText = (TextView) findViewById(R.id.start_text);
        endText = (TextView) findViewById(R.id.end_text);

        endText.setText(String.valueOf(maxLineWidth));
        seekBarValueControl.setProgress(currentLineWidth);
        updatePreview();

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
        imageViewAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLineWidth = Math.min(20, currentLineWidth + 1);
                seekBarValueControl.setProgress(currentLineWidth);
                updatePreview();
            }
        });
        imageViewMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLineWidth = Math.max(1, currentLineWidth - 1);
                seekBarValueControl.setProgress(currentLineWidth);
                updatePreview();
            }
        });
        initSeerProgress();

        seekBarValueControl.post(new Runnable() {
            @Override
            public void run() {
                startText.setX(seekBarValueControl.getLeft() + DimenUtils.dip2px(getContext(), 10));
                endText.setX(seekBarValueControl.getRight() - DimenUtils.dip2px(getContext(), 10) - endText.getMeasuredWidth() /2);
            }
        });
    }

    private void initSeerProgress() {
        seekBarValueControl.setMax(maxLineWidth);
        seekBarValueControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                currentLineWidth = progress;
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updatePreview() {
        circleView.setSize(currentLineWidth);
        besselCurveView.setSize(currentLineWidth);
    }
}
