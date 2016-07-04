package com.onyx.android.note.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.note.R;


/**
 * Created by solskjaer49 on 16/6/30 20:38.
 */

public class PenWidthPopupMenu extends PopupWindow {
    private ImageView addBtn, minusBtn;
    private SeekBar penWidthControlSeekbar;
    private int currentValue;
    private PopupMenuCallback callback;
    private View parentView;
    private int displayLocX, displayLocY;
    private int minValue, maxValue;

    public interface PopupMenuCallback {
        void onValueChanged(int newValue);
    }

    public PenWidthPopupMenu(Context context, LayoutInflater inflater, int curValue, int minValue,
                             int maxValue, View parentView, int locX, int locY, PopupMenuCallback menuCallback) {
        super(inflater.inflate(R.layout.pen_width_popup_layout, null),
                ViewGroup.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelSize(R.dimen.pen_width_popup_height));
        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        setOutsideTouchable(true);
        TextView minValueIndicator, maxValueIndicator;
        this.callback = menuCallback;
        this.currentValue = curValue;
        this.parentView = parentView;
        this.displayLocX = locX;
        this.displayLocY = locY;
        this.minValue = minValue;
        this.maxValue = maxValue;
        addBtn = (ImageView) getContentView().findViewById(R.id.add_btn);
        minusBtn = (ImageView) getContentView().findViewById(R.id.minus_btn);
        minValueIndicator = (TextView) getContentView().findViewById(R.id.min_value);
        maxValueIndicator = (TextView) getContentView().findViewById(R.id.max_value);
        penWidthControlSeekbar = (SeekBar) getContentView().findViewById(R.id.note_width_seek_bar);
        minValueIndicator.setText(Integer.toString(this.minValue));
        maxValueIndicator.setText(Integer.toString(this.maxValue));
        penWidthControlSeekbar.setMax(maxValue - minValue);
        penWidthControlSeekbar.setProgress(curValue - minValue);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepChangeValue(1);
                if (callback != null) {
                    callback.onValueChanged(currentValue);
                }
            }
        });
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepChangeValue(-1);
                if (callback != null) {
                    callback.onValueChanged(currentValue);
                }
            }
        });
        penWidthControlSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValue = currentValue + progress;
                if (callback != null) {
                    callback.onValueChanged(currentValue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void stepChangeValue(int stepValue) {
        currentValue += stepValue;
    }

    public void toggleStatus() {
        if (isShowing()) {
            dismiss();
        } else {
            showAtLocation(parentView, Gravity.NO_GRAVITY, displayLocX, displayLocY);
        }
    }

    public void show() {
        if (!isShowing()) {
            showAtLocation(parentView, Gravity.NO_GRAVITY, displayLocX, displayLocY);
        }
    }


}
