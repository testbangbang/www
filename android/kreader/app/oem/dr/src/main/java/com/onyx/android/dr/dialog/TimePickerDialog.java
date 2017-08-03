package com.onyx.android.dr.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.util.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhouzhiming on 2017/8/2.
 */
public class TimePickerDialog {
    private Context context;
    private AlertDialog.Builder alertDialog;
    private int hour, minute;
    private TimePickerDialogInterface timePickerDialogInterface;
    private TimePicker timePicker;
    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private DatePicker datePicker;
    private int tag = 0;
    private int year, day, month;
    private String timeHorizon;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private long startDateMillisecond;
    private long endDateMillisecond;

    public TimePickerDialog(Context context) {
        super();
        this.context = context;
        timePickerDialogInterface = (TimePickerDialogInterface) context;
    }

    private View initStartAndEndDatePicker() {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.dialog_start_and_end_date_picker, null);
        startDatePicker = (DatePicker) inflate
                .findViewById(R.id.start_date_picker);
        endDatePicker = (DatePicker) inflate
                .findViewById(R.id.end_date_picker);
        resizePikcer(startDatePicker);
        resizePikcer(endDatePicker);
        return inflate;
    }

    private View initStartAndEndTimePicker() {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.dialog_start_and_end_time_picker, null);
        startTimePicker = (TimePicker) inflate
                .findViewById(R.id.start_time_picker);
        endTimePicker = (TimePicker) inflate
                .findViewById(R.id.end_time_picker);
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);
        resizePikcer(startTimePicker);
        resizePikcer(endTimePicker);
        return inflate;
    }

    private View initDateAndTimePicker() {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.dialog_date_and_time_picker, null);
        timePicker = (TimePicker) inflate
                .findViewById(R.id.time_picker);
        datePicker = (DatePicker) inflate
                .findViewById(R.id.date_picker);
        timePicker.setIs24HourView(true);
        resizePikcer(timePicker);
        resizePikcer(datePicker);
        return inflate;
    }

    private void initDialog(View view) {
        alertDialog.setPositiveButton(context.getString(R.string.time_selector_dialog_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (tag == 0) {
                            getTimePickerValue();
                        } else if (tag == 1) {
                            getDatePickerValue();
                        } else if (tag == 2) {
                            getDateAndPickerValue();
                        }
                        timePickerDialogInterface.positiveListener();
                    }
                });
        alertDialog.setNegativeButton(context.getString(R.string.time_selector_dialog_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setView(view);
    }

    public void showTimePickerDialog() {
        tag = 0;
        View view = initStartAndEndTimePicker();
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.select_time);
        initDialog(view);
        alertDialog.show();
    }

    public void showDatePickerDialog() {
        tag = 1;
        View view = initStartAndEndDatePicker();
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.select_date);
        initDialog(view);
        alertDialog.show();
    }

    public void showDateAndTimePickerDialog() {
        tag = 2;
        View view = initDateAndTimePicker();
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.select_time);
        initDialog(view);
        alertDialog.show();
    }

    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    private void getDateAndPickerValue() {
        year = datePicker.getYear();
        month = datePicker.getMonth();
        day = datePicker.getDayOfMonth();
        hour = timePicker.getCurrentHour();
        minute = timePicker.getCurrentMinute();
    }

    private void getTimePickerValue() {
        int startHour = startTimePicker.getCurrentHour();
        int startMinute = startTimePicker.getCurrentMinute();
        int endHour = endTimePicker.getCurrentHour();
        int endMinute = endTimePicker.getCurrentMinute();
        String startTime = Utils.getTimeAndMinuteSecond(startHour, startMinute);
        String endTime = Utils.getTimeAndMinuteSecond(endHour, endMinute);
        boolean compareTime = TimeUtils.compareTime(startTime, endTime);
        if (compareTime) {
            timeHorizon = Utils.getTimeQuantum(startTime, endTime);
        } else {
            CommonNotices.showMessage(context, context.getString(R.string.end_time_more_than_start_time));
        }
    }

    private void getDatePickerValue() {
        int startYear = startDatePicker.getYear();
        int startMonth = startDatePicker.getMonth() + 1;
        int startDay = startDatePicker.getDayOfMonth();
        int endYear = endDatePicker.getYear();
        int endMonth = endDatePicker.getMonth() + 1;
        int endDay = endDatePicker.getDayOfMonth();
        String startDateString = TimeUtils.getDateString(startYear, startMonth, startDay);
        String endDateString = TimeUtils.getDateString(endYear, endMonth, endDay);
        int tag = TimeUtils.compareDate(startDateString, endDateString);
        if (tag == 1) {
            CommonNotices.showMessage(context, context.getString(R.string.end_date_must_more_than_start_date));
        }else{
            startDateMillisecond = TimeUtils.getStartDateMillisecond(startDateString);
            endDateMillisecond = TimeUtils.getEndDateMillisecond(endDateString);
        }
    }

    public interface TimePickerDialogInterface {
        void positiveListener();
    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month + 1;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public String getTimeHorizon() {
        return timeHorizon;
    }

    public long getStartDateMillisecond() {
        return startDateMillisecond;
    }

    public long getEndDateMillisecond() {
        return endDateMillisecond;
    }
}

