package com.onyx.android.dr.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DictLanguageTypeAdapter;
import com.onyx.android.dr.bean.LanguageTypeBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.util.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhouzhiming on 2017/9/29.
 */
public class ReadingRateDialog {
    private Context context;
    private AlertDialog.Builder alertDialog;
    private int hour, minute;
    private ReadingRateDialogInterface readingRateDialogInterface;
    private TimePicker timePicker;
    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private DatePicker datePicker;
    private int year, day, month;
    private String timeHorizon;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private long startDateMillisecond;
    private long endDateMillisecond;
    private Spinner spinner;
    private DictLanguageTypeAdapter languageTypeAdapter;
    private List<LanguageTypeBean> spinnerData = new ArrayList<>();
    public int firstSequenceType = Constants.ENGLISH_TYPE;
    public String language;
    private int number = 3;
    private String confirm = "";

    public ReadingRateDialog(Context context) {
        super();
        this.context = context;
        readingRateDialogInterface = (ReadingRateDialogInterface) context;
    }

    public void showDatePickerDialog(int type) {
        View view = initStartAndEndDatePicker();
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.select_time_range);
        initDialog(type, view);
        alertDialog.show();
    }

    private View initStartAndEndDatePicker() {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.dialog_reading_rate, null);
        startDatePicker = (DatePicker) inflate
                .findViewById(R.id.start_date_picker);
        endDatePicker = (DatePicker) inflate
                .findViewById(R.id.end_date_picker);
        spinner = (Spinner) inflate
                .findViewById(R.id.reading_rate_dialog_spinner);
        resizePicker(startDatePicker);
        resizePicker(endDatePicker);
        return inflate;
    }

    private void initDialog(final int type, View view) {
        if (type == Constants.READING_RATE_DIALOG_EXPORT) {
            confirm = context.getString(R.string.export);
        } else if(type == Constants.READING_RATE_DIALOG_SHARE) {
            confirm = context.getString(R.string.select_group);
        }
        initSpinnerData();
        alertDialog.setPositiveButton(confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getDatePickerValue();
                        readingRateDialogInterface.positiveListener(type);
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
        initEvent();
    }

    private void initSpinnerData() {
        languageTypeAdapter = new DictLanguageTypeAdapter(context);
        spinnerData = Utils.getSpinnerData();
        languageTypeAdapter.setDatas(spinnerData);
        spinner.setAdapter(languageTypeAdapter);
        spinner.setSelection(number);
        language = context.getResources().getString(R.string.all);
    }

    private void initEvent() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LanguageTypeBean languageTypeBean = spinnerData.get(position);
                firstSequenceType = languageTypeBean.getType();
                language = languageTypeBean.getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    private void resizePicker(FrameLayout tp) {
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

    public interface ReadingRateDialogInterface {
        void positiveListener(int type);
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

    public int getFirstSequenceType() {
        return firstSequenceType;
    }

    public void setFirstSequenceType(int firstSequenceType) {
        this.firstSequenceType = firstSequenceType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

