package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.view.BesselCurveView;
import com.onyx.android.sdk.ui.view.CircleView;


/**
 * Created by ming on 2016/11/24.
 */

public class DialogCustomLineWidth extends Dialog {

    private ImageView imageViewMinusButton;
    private SeekBar seekBarValueControl;
    private ImageView imageViewAddButton;
    private Button btnCancel;
    private Button btnOk;
    private LinearLayout previewLayout;
    private CircleView circleView;
    private BesselCurveView besselCurveView;

    private int lineWidth;
    private int color;

    public DialogCustomLineWidth(Context context, final int lineWidth, final int color) {
        super(context);
        this.lineWidth = lineWidth;
        this.color = color;
        setContentView(R.layout.dialog_custom_line_width);
        initView();
    }

    private void initView() {
        imageViewMinusButton = (ImageView) findViewById(R.id.imageView_MinusButton);
        seekBarValueControl = (SeekBar) findViewById(R.id.seekBar_valueControl);
        imageViewAddButton = (ImageView) findViewById(R.id.imageView_AddButton);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);
        previewLayout = (LinearLayout) findViewById(R.id.preview_layout);

        circleView = new CircleView(getContext(), color, lineWidth);
        besselCurveView = new BesselCurveView(getContext());
//        previewLayout.addView(circleView);
//        previewLayout.addView(besselCurveView);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                lineWidth = Math.min(20, lineWidth - 1);
                seekBarValueControl.setProgress(lineWidth);
            }
        });
        imageViewMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineWidth = Math.max(1, lineWidth - 1);
                seekBarValueControl.setProgress(lineWidth);
            }
        });
        initSeerProgress();
    }

    private void initSeerProgress() {
        seekBarValueControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
//                circleView.setSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
