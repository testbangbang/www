package com.onyx.android.dr.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.presenter.MemorandumPresenter;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MemorandumActivity extends BaseActivity implements MemorandumView, TimePickerDialog.TimePickerDialogInterface {
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.memorandum_activity_sunday_time)
    TextView sundayTime;
    @Bind(R.id.memorandum_activity_monday_time)
    TextView mondayTime;
    @Bind(R.id.memorandum_activity_tuesday_time)
    TextView tuesdayTime;
    @Bind(R.id.memorandum_activity_wednesday_time)
    TextView wednesdayTime;
    @Bind(R.id.memorandum_activity_thursday_time)
    TextView thursdayTime;
    @Bind(R.id.memorandum_activity_friday_time)
    TextView fridayTime;
    @Bind(R.id.memorandum_activity_saturday_time)
    TextView saturdayTime;
    @Bind(R.id.memorandum_activity_sunday_matter)
    TextView sundayMatter;
    @Bind(R.id.memorandum_activity_monday_matter)
    TextView mondayMatter;
    @Bind(R.id.memorandum_activity_tuesday_matter)
    TextView tuesdayMatter;
    @Bind(R.id.memorandum_activity_wednesday_matter)
    TextView wednesdayMatter;
    @Bind(R.id.memorandum_activity_thursday_matter)
    TextView thursdayMatter;
    @Bind(R.id.memorandum_activity_friday_matter)
    TextView fridayMatter;
    @Bind(R.id.memorandum_activity_saturday_matter)
    TextView saturdayMatter;
    @Bind(R.id.memorandum_activity_sunday_date_of_week)
    TextView dateOfWeek;
    @Bind(R.id.memorandum_activity_last_week)
    TextView lastWeek;
    @Bind(R.id.memorandum_activity_next_week)
    TextView nextWeek;
    @Bind(R.id.memorandum_activity_sunday_container)
    LinearLayout sundayContainer;
    @Bind(R.id.memorandum_activity_monday_container)
    LinearLayout mondayContainer;
    @Bind(R.id.memorandum_activity_tuesday_container)
    LinearLayout tuesdayContainer;
    @Bind(R.id.memorandum_activity_wednesday_container)
    LinearLayout wednesdayContainer;
    @Bind(R.id.memorandum_activity_thursday_container)
    LinearLayout thursdayContainer;
    @Bind(R.id.memorandum_activity_friday_container)
    LinearLayout fridayContainer;
    @Bind(R.id.memorandum_activity_saturday_container)
    LinearLayout saturdayContainer;
    private MemorandumPresenter memorandumPresenter;
    private List<MemorandumEntity> memorandumList;
    private int number = 7;
    private String sundayMatterContent = "";
    private String mondayMatterContent = "";
    private long sundayCurrentTime = 0;
    private long mondayCurrentTime = 0;
    private String tuesdayMatterContent = "";
    private long tuesdayCurrentTime = 0;
    private String saturdayMatterContent = "";
    private long saturdayCurrentTime = 0;
    private String fridayMatterContent = "";
    private long thursdayCurrentTime = 0;
    private String thursdayMatterContent = "";
    private String wednesdayMatterContent = "";
    private long wednesdayCurrentTime = 0;
    private long fridayCurrentTime = 0;
    private TimePickerDialog timePickerDialog;
    private String currentDate;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_memorandum;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        memorandumList = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
        initTitleData();
        setTimeData();
    }

    private void setTimeData() {
        currentDate = TimeUtils.getDate(System.currentTimeMillis());
        memorandumList = TimeUtils.printWeekAndDay(currentDate);
    }

    private void setDate() {
        sundayTime.setText(memorandumList.get(0).getDate());
        mondayTime.setText(memorandumList.get(1).getDate());
        tuesdayTime.setText(memorandumList.get(2).getDate());
        wednesdayTime.setText(memorandumList.get(3).getDate());
        thursdayTime.setText(memorandumList.get(4).getDate());
        fridayTime.setText(memorandumList.get(4).getDate());
        saturdayTime.setText(memorandumList.get(6).getDate());
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.memorandum);
        title.setText(getString(R.string.memorandum));
    }

    @Override
    protected void onResume() {
        super.onResume();
        memorandumPresenter = new MemorandumPresenter(getApplicationContext(), this);
        getMemorandumData();
    }

    private void getMemorandumData() {
        if (memorandumList != null && memorandumList.size() >= number) {
            setDate();
            resetMatterData();
            dateOfWeek.setText(memorandumList.get(0).getDate() + " 至 " + memorandumList.get(6).getDate());
            long startDateMillisecond = TimeUtils.getStartDateMillisecond(memorandumList.get(0).getDate());
            long endDateMillisecond = TimeUtils.getEndDateMillisecond(memorandumList.get(6).getDate());
            memorandumPresenter.getMemorandumDataByTime(startDateMillisecond, endDateMillisecond);
        }
    }

    @Override
    public void setMemorandumData(List<MemorandumEntity> dataList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            String currentTime = dataList.get(i).date;
            int week = Utils.getWeek(currentTime);
            switch (week) {
                case Constants.SUNDAY:
                    sundayMatterContent = dataList.get(i).getMatter();
                    sundayCurrentTime = dataList.get(i).currentTime;
                    sundayMatter.setText(sundayMatterContent);
                    break;
                case Constants.MONDAY:
                    mondayMatterContent = dataList.get(i).getMatter();
                    mondayCurrentTime = dataList.get(i).currentTime;
                    mondayMatter.setText(mondayMatterContent);
                    break;
                case Constants.TUESDAY:
                    tuesdayMatterContent = dataList.get(i).getMatter();
                    tuesdayCurrentTime = dataList.get(i).currentTime;
                    tuesdayMatter.setText(tuesdayMatterContent);
                    break;
                case Constants.WEDNESDAY:
                    wednesdayMatterContent = dataList.get(i).getMatter();
                    wednesdayCurrentTime = dataList.get(i).currentTime;
                    wednesdayMatter.setText(wednesdayMatterContent);
                    break;
                case Constants.THURSDAY:
                    thursdayMatterContent = dataList.get(i).getMatter();
                    thursdayCurrentTime = dataList.get(i).currentTime;
                    thursdayMatter.setText(thursdayMatterContent);
                    break;
                case Constants.FRIDAY:
                    fridayMatterContent = dataList.get(i).getMatter();
                    fridayCurrentTime = dataList.get(i).currentTime;
                    fridayMatter.setText(fridayMatterContent);
                    break;
                case Constants.SATURDAY:
                    saturdayMatterContent = dataList.get(i).getMatter();
                    saturdayCurrentTime = dataList.get(i).currentTime;
                    saturdayMatter.setText(saturdayMatterContent);
                    break;
            }
        }
    }

    private void resetMatterData() {
        sundayMatter.setText(getString(R.string.nothing));
        mondayMatter.setText(getString(R.string.nothing));
        tuesdayMatter.setText(getString(R.string.nothing));
        wednesdayMatter.setText(getString(R.string.nothing));
        thursdayMatter.setText(getString(R.string.nothing));
        fridayMatter.setText(getString(R.string.nothing));
        saturdayMatter.setText(getString(R.string.nothing));
        sundayMatterContent = "";
        mondayMatterContent = "";
        tuesdayMatterContent = "";
        saturdayMatterContent = "";
        fridayMatterContent = "";
        thursdayMatterContent = "";
        wednesdayMatterContent = "";
    }

    @OnClick({R.id.menu_back,
            R.id.memorandum_activity_sunday_container,
            R.id.memorandum_activity_monday_container,
            R.id.memorandum_activity_tuesday_container,
            R.id.memorandum_activity_wednesday_container,
            R.id.memorandum_activity_thursday_container,
            R.id.memorandum_activity_friday_container,
            R.id.memorandum_activity_sunday_date_of_week,
            R.id.memorandum_activity_last_week,
            R.id.memorandum_activity_next_week,
            R.id.memorandum_activity_saturday_container})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.memorandum_activity_sunday_date_of_week:
                timePickerDialog.showOnlyDateDialog();
                break;
            case R.id.memorandum_activity_last_week:
                currentDate = TimeUtils.getLastDateString(currentDate);
                memorandumList = TimeUtils.printWeekAndDay(currentDate);
                getMemorandumData();
                break;
            case R.id.memorandum_activity_next_week:
                currentDate = TimeUtils.getNextDateString(currentDate);
                memorandumList = TimeUtils.printWeekAndDay(currentDate);
                getMemorandumData();
                break;
            case R.id.memorandum_activity_sunday_container:
                startAddMemorandum(memorandumList.get(0).getDayOfWeek(), memorandumList.get(0).getDate(), sundayMatterContent, sundayCurrentTime);
                break;
            case R.id.memorandum_activity_monday_container:
                startAddMemorandum(memorandumList.get(1).getDayOfWeek(), memorandumList.get(1).getDate(), mondayMatterContent, mondayCurrentTime);
                break;
            case R.id.memorandum_activity_tuesday_container:
                startAddMemorandum(memorandumList.get(2).getDayOfWeek(), memorandumList.get(2).getDate(), tuesdayMatterContent, tuesdayCurrentTime);
                break;
            case R.id.memorandum_activity_wednesday_container:
                startAddMemorandum(memorandumList.get(3).getDayOfWeek(), memorandumList.get(3).getDate(), wednesdayMatterContent, wednesdayCurrentTime);
                break;
            case R.id.memorandum_activity_thursday_container:
                startAddMemorandum(memorandumList.get(4).getDayOfWeek(), memorandumList.get(4).getDate(), thursdayMatterContent, thursdayCurrentTime);
                break;
            case R.id.memorandum_activity_friday_container:
                startAddMemorandum(memorandumList.get(5).getDayOfWeek(), memorandumList.get(5).getDate(), fridayMatterContent, fridayCurrentTime);
                break;
            case R.id.memorandum_activity_saturday_container:
                startAddMemorandum(memorandumList.get(6).getDayOfWeek(), memorandumList.get(6).getDate(), saturdayMatterContent, saturdayCurrentTime);
                break;
        }
    }

    private void startAddMemorandum(String dayOfWeek, String date, String matter, long currentTime) {
        Intent intent = new Intent();
        intent.putExtra(Constants.MEMORANDUM_DAY_OF_WEEK, dayOfWeek);
        intent.putExtra(Constants.MEMORANDUM_TIME, date);
        intent.putExtra(Constants.MEMORANDUM_MATTER, matter);
        intent.putExtra(Constants.MEMORANDUM_CURRENT_TIME, currentTime);
        ActivityManager.startAddMemorandumActivity(DRApplication.getInstance(), intent);
        finish();
    }

    @Override
    public void positiveListener() {
        currentDate = timePickerDialog.getOnlyDateString();
        memorandumList = TimeUtils.printWeekAndDay(currentDate);
        getMemorandumData();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
