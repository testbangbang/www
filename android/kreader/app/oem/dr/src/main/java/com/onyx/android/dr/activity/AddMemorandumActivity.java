package com.onyx.android.dr.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.HourAdapter;
import com.onyx.android.dr.adapter.MinuteAdapter;
import com.onyx.android.dr.bean.MemorandumBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.dialog.SelectAlertDialog;
import com.onyx.android.dr.event.HourEvent;
import com.onyx.android.dr.event.MinuteEvent;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.presenter.MemorandumPresenter;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.DefaultEditText;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/7/21.
 */
public class AddMemorandumActivity extends BaseActivity implements MemorandumView {
    @Bind(R.id.add_memorandum_activity_start_time)
    TextView startTime;
    @Bind(R.id.add_memorandum_activity_end_time)
    TextView endTime;
    @Bind(R.id.title_bar_right_menu)
    TextView rightMenu;
    @Bind(R.id.add_memorandum_activity_content)
    DefaultEditText contentEditText;
    private MemorandumPresenter memorandumPresenter;
    private SelectAlertDialog selectTimeDialog;
    private TextView hour;
    private TextView minute;
    private PageRecyclerView hourRecyclerView;
    private PageRecyclerView minuteRecyclerView;
    private List<String> hourList;
    private ArrayList<String> minuteList;
    private MinuteAdapter minuteAdapter;
    private HourAdapter hourAdapter;
    private TextView cancel;
    private TextView confirm;
    private String hourString = "";
    private String minuteString = "";
    private String startTimeString = "";
    private String endTimeString = "";
    private boolean startOrEnd;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_add_memorandum;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        loadDialog();
    }

    @Override
    protected void initData() {
        memorandumPresenter = new MemorandumPresenter(getApplicationContext(), this);
        hourList = new ArrayList<String>();
        minuteList = new ArrayList<String>();
        hourAdapter = new HourAdapter();
        minuteAdapter = new MinuteAdapter();
        setTitleData();
        loadHourAndMinuteDatas();
        initEvent();
    }

    private void setTitleData() {
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText(R.string.save_button);
        rightMenu.setTextSize(getResources().getDimension(R.dimen.level_two_font_size));
    }

    private void loadHourAndMinuteDatas() {
        for (int i = 1; i <= Constants.HOUR; i++) {
            hourList.add(String.valueOf(i));
        }
        for (int i = 1; i <= Constants.MINUTE; i++) {
            minuteList.add(String.valueOf(i));
        }
    }

    @Override
    public void setMemorandumData(List<MemorandumEntity> dataList) {
    }

    private void loadDialog() {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_time_selector, null);
        selectTimeDialog = new SelectAlertDialog(this);
        // find id
        hour = (TextView) view.findViewById(R.id.time_selector_dialog_hour);
        minute = (TextView) view.findViewById(R.id.time_selector_dialog_minute);
        cancel = (TextView) view.findViewById(R.id.time_selector_dialog_cancel);
        confirm = (TextView) view.findViewById(R.id.time_selector_dialog_confirm);
        hourRecyclerView = (PageRecyclerView) view.findViewById(R.id.date_selector_dialog_hour_recyclerview);
        minuteRecyclerView = (PageRecyclerView) view.findViewById(R.id.time_selector_dialog_minute_recyclerview);

        WindowManager.LayoutParams attributes = selectTimeDialog.getWindow().getAttributes();
        attributes.height = (int) (Utils.getScreenHeight(DRApplication.getInstance()) * 0.3);
        attributes.width = (int) (Utils.getScreenWidth(DRApplication.getInstance()) * 0.8);
        selectTimeDialog.getWindow().setAttributes(attributes);
        selectTimeDialog.setView(view);
    }

    private void initEvent() {
    }

    @OnClick({R.id.add_memorandum_activity_start_time,
            R.id.image_view_back,
            R.id.add_memorandum_activity_end_time,
            R.id.title_bar_right_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.title_bar_right_menu:
                insertData();
                break;
            case R.id.add_memorandum_activity_start_time:
                startOrEnd = true;
                loadDialogData();
                break;
            case R.id.add_memorandum_activity_end_time:
                startOrEnd = false;
                loadDialogData();
                break;
        }
    }

    private void loadDialogData() {
        hourAdapter.setDatas(hourList);
        minuteAdapter.setDatas(minuteList);
        hourRecyclerView.setAdapter(hourAdapter);
        minuteRecyclerView.setAdapter(minuteAdapter);

        hourAdapter.setOnItemClick(new HourAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                hourString = hourList.get(position);
                EventBus.getDefault().post(new HourEvent());
            }
        });
        minuteAdapter.setOnItemClick(new MinuteAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                minuteString = minuteList.get(position);
                EventBus.getDefault().post(new MinuteEvent());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectTimeDialog.isShowing()) {
                    selectTimeDialog.cancel();
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSelectTime();
            }
        });
        selectTimeDialog.show();
    }

    private void insertData() {
        String content = contentEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(startTimeString)) {
            CommonNotices.showMessage(this, getString(R.string.select_start_time));
            return;
        }
        if (StringUtils.isNullOrEmpty(endTimeString)) {
            CommonNotices.showMessage(this, getString(R.string.select_end_time));
            return;
        }
        if (StringUtils.isNullOrEmpty(content)) {
            CommonNotices.showMessage(this, getString(R.string.input_memorandum));
            return;
        }
        MemorandumBean bean = new MemorandumBean();
        String timeQuantum = Utils.getTimeQuantum(startTimeString, endTimeString);
        bean.setTimeQuantum(timeQuantum);
        bean.setMatter(content);
        memorandumPresenter.insertMemorandum(bean);
        finish();
    }

    private void confirmSelectTime() {
        if (StringUtils.isNullOrEmpty(hourString)) {
            CommonNotices.showMessage(AddMemorandumActivity.this, getString(R.string.select_hour));
            return;
        }
        if (StringUtils.isNullOrEmpty(minuteString)) {
            CommonNotices.showMessage(AddMemorandumActivity.this, getString(R.string.select_minute));
            return;
        }
        if (startOrEnd) {
            startTimeString = Utils.getTimeAndMinuteSecond(hourString, minuteString);
            if (StringUtils.isNullOrEmpty(endTimeString)) {
                startTime.setText(startTimeString);
            } else {
                boolean compareTime = TimeUtils.compareTime(startTimeString, endTimeString);
                if (compareTime) {
                    startTime.setText(startTimeString);
                } else {
                    CommonNotices.showMessage(AddMemorandumActivity.this, getString(R.string.start_time_less_than_end_time));
                }
            }
        } else {
            endTimeString = Utils.getTimeAndMinuteSecond(hourString, minuteString);
            if (StringUtils.isNullOrEmpty(endTimeString)) {
                endTime.setText(endTimeString);
            } else {
                boolean compareTime = TimeUtils.compareTime(startTimeString, endTimeString);
                if (compareTime) {
                    endTime.setText(endTimeString);
                } else {
                    CommonNotices.showMessage(AddMemorandumActivity.this, getString(R.string.end_time_more_than_start_time));
                }
            }
        }
        if (selectTimeDialog.isShowing()) {
            selectTimeDialog.cancel();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHourEvent(HourEvent event) {
        if (StringUtils.isNullOrEmpty(hourString)) {
            hour.setText(getString(R.string.time_selector_dialog_hour));
        } else {
            hour.setText(hourString + getString(R.string.time_selector_dialog_hour));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMinuteEvent(MinuteEvent event) {
        if (StringUtils.isNullOrEmpty(minuteString)) {
            minute.setText(getString(R.string.time_selector_dialog_minute));
        } else {
            minute.setText(minuteString + getString(R.string.time_selector_dialog_minute));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
