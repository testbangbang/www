package com.onyx.android.sun.view;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.onyx.android.sun.R;
import com.onyx.android.sun.fragment.HomeWorkFragment;

import java.util.Calendar;

/**
 * Created by li on 2017/10/9.
 */

public class TimePickerDialog extends Dialog implements View.OnClickListener {
    private  int id;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private int hour;
    private int minute;
    private int second;
    private int year;
    private int month;
    private int day;
    private TextView certain;
    private TimePickerDialogInterface listener;

    public TimePickerDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.date_picker_layout);
        initView();
    }

    public void setId(int id) {
        this.id = id;
    }

    private void initView() {
        datePicker = (DatePicker) findViewById(R.id.sp_date);
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

        timePicker = (TimePicker) findViewById(R.id.sp_time);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                TimePickerDialog.this.year = year;
                TimePickerDialog.this.month = monthOfYear;
                TimePickerDialog.this.day = dayOfMonth;
            }
        });

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                TimePickerDialog.this.hour = hourOfDay;
                TimePickerDialog.this.minute = minute;
            }
        });

        certain = (TextView) findViewById(R.id.sp_certain);
        certain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        listener.positiveListener();
    }

    public String getDateTime() {
        return year + "-" + (month + 1) + "-" + day;
    }

    public void setDialogInterface(TimePickerDialogInterface listener) {
        this.listener = listener;
    }

    public int getId() {
        return id;
    }

    public interface TimePickerDialogInterface {
        public void positiveListener();
    }
}
