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
import com.onyx.android.dr.common.Constants;
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
    private TimePickerDialogInterface timePickerDialogInterface;
    private TimePicker timePicker;
    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private DatePicker datePicker;
    private int tag = 0;
    private String timeHorizon;
    private String dateAndTimeHorizon;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private long startDateMillisecond;
    private long endDateMillisecond;
    private TimePicker startTimePickerAll;
    private DatePicker startDatePickerAll;
    private TimePicker endTimePickerAll;
    private DatePicker endDatePickerAll;

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
        startTimePickerAll = (TimePicker) inflate
                .findViewById(R.id.date_and_time_dialog_start_time_picker);
        startDatePickerAll = (DatePicker) inflate
                .findViewById(R.id.date_and_time_dialog_start_date_picker);
        endTimePickerAll = (TimePicker) inflate
                .findViewById(R.id.date_and_time_dialog_end_time_picker);
        endDatePickerAll = (DatePicker) inflate
                .findViewById(R.id.date_and_time_dialog_end_date_picker);
        startTimePickerAll.setIs24HourView(true);
        endTimePickerAll.setIs24HourView(true);
        resizePikcer(startTimePickerAll);
        resizePikcer(endTimePickerAll);
        resizePikcer(startDatePickerAll);
        resizePikcer(endDatePickerAll);
        return inflate;
    }

    private void initDialog(View view) {
        alertDialog.setPositiveButton(context.getString(R.string.time_selector_dialog_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (tag == Constants.DIALOG_VIEW_FIRST_TYPE) {
                            getTimePickerValue();
                        } else if (tag == Constants.DIALOG_VIEW_SECOND_TYPE) {
                            getDatePickerValue();
                        } else if (tag == Constants.DIALOG_VIEW_THIRD_TYPE) {
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
        tag = Constants.DIALOG_VIEW_FIRST_TYPE;
        View view = initStartAndEndTimePicker();
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.select_time);
        initDialog(view);
        alertDialog.show();
    }

    public void showDatePickerDialog() {
        tag = Constants.DIALOG_VIEW_SECOND_TYPE;
        View view = initStartAndEndDatePicker();
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.select_date);
        initDialog(view);
        alertDialog.show();
    }

    public void showDateAndTimePickerDialog() {
        tag = Constants.DIALOG_VIEW_THIRD_TYPE;
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
        int startYear = startDatePickerAll.getYear();
        int startMonth = startDatePickerAll.getMonth() + 1;
        int startDay = startDatePickerAll.getDayOfMonth();
        int endYear = endDatePickerAll.getYear();
        int endMonth = endDatePickerAll.getMonth() + 1;
        int endDay = endDatePickerAll.getDayOfMonth();
        int startHour = startTimePickerAll.getCurrentHour();
        int startMinute = startTimePickerAll.getCurrentMinute();
        int endHour = endTimePickerAll.getCurrentHour();
        int endMinute = endTimePickerAll.getCurrentMinute();
        String startTime = Utils.getTimeAndMinuteSecond(startHour, startMinute);
        String endTime = Utils.getTimeAndMinuteSecond(endHour, endMinute);
        String startDateString = TimeUtils.getDateAndTimeString(startYear, startMonth, startDay, startTime);
        String endDateString = TimeUtils.getDateAndTimeString(endYear, endMonth, endDay, endTime);
        String secondStartDateString = TimeUtils.getSecondDateAndTimeString(startYear, startMonth, startDay, startTime);
        String secondEndDateString = TimeUtils.getSecondDateAndTimeString(endYear, endMonth, endDay, endTime);
        boolean tag = TimeUtils.compareDateAndTime(secondStartDateString, secondEndDateString);
        if (tag) {
            dateAndTimeHorizon = Utils.getDateAndTimeQuantum(startDateString, endDateString);
        }else{
            CommonNotices.showMessage(context, context.getString(R.string.end_date_must_more_than_start_date));
        }
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

    public String getTimeHorizon() {
        return timeHorizon;
    }

    public String getDateAndTimeHorizon() {
        return dateAndTimeHorizon;
    }

    public long getStartDateMillisecond() {
        return startDateMillisecond;
    }

    public long getEndDateMillisecond() {
        return endDateMillisecond;
    }
}

