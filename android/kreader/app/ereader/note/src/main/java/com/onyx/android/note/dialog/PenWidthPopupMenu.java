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
    private SeekBar penWidthControlSeekBar;

    public int getCurrentValue() {
        return currentValue;
    }

    private int currentValue;
    private PopupMenuCallback callback;
    private View parentView;
    private int displayLocX, displayLocY;
    private TextView currentTextSizeIndicator;
    private int minValue, maxValue;
    //TODO:use hard code string for test show.15-40 is too large for writing.
    private static final int visualMinValue = 15;
    private static final int visualMaxValue = 40;

    public interface PopupMenuCallback {
        void onValueChanged(int newValue);
    }

    public PenWidthPopupMenu(Context context, LayoutInflater inflater, final int curValue, final int minValue,
                             int maxValue, View parentView, int locX, int locY, PopupMenuCallback menuCallback) {
        super(inflater.inflate(R.layout.pen_width_popup_layout, null),
                ViewGroup.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelSize(R.dimen.pen_width_popup_height));
        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        setOutsideTouchable(true);
        setFocusable(true);
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
        currentTextSizeIndicator = (TextView) getContentView().findViewById(R.id.current_value);
        penWidthControlSeekBar = (SeekBar) getContentView().findViewById(R.id.note_width_seek_bar);
        currentTextSizeIndicator.setText(Integer.toString(visualMinValue + currentValue - minValue));
        minValueIndicator.setText(Integer.toString(visualMinValue));
        maxValueIndicator.setText(Integer.toString(visualMaxValue));
        penWidthControlSeekBar.setMax(maxValue - minValue);
        penWidthControlSeekBar.setProgress(curValue - minValue);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepChangeValue(1);
                penWidthControlSeekBar.setProgress(currentValue);
                if (callback != null) {
                    callback.onValueChanged(currentValue);
                }
            }
        });
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepChangeValue(-1);
                penWidthControlSeekBar.setProgress(currentValue);
                if (callback != null) {
                    callback.onValueChanged(currentValue);
                }
            }
        });
        penWidthControlSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTextSizeIndicator.setText(Integer.toString(visualMinValue + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentValue = minValue + seekBar.getProgress();
                if (callback != null) {
                    callback.onValueChanged(currentValue);
                }
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
