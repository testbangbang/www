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
import com.onyx.android.dr.dialog.SelectAlertDialog;
import com.onyx.android.dr.event.HourEvent;
import com.onyx.android.dr.event.MinuteEvent;
import com.onyx.android.dr.interfaces.AddMemorandumView;
import com.onyx.android.dr.presenter.AddMemorandumPresenter;
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
public class AddMemorandumActivity extends BaseActivity implements AddMemorandumView {
    @Bind(R.id.add_memorandum_activity_start_time)
    TextView startTime;
    @Bind(R.id.add_memorandum_activity_end_time)
    TextView endTime;
    @Bind(R.id.title_bar_right_menu)
    TextView rightMenu;
    @Bind(R.id.add_memorandum_activity_content)
    DefaultEditText contentEditText;
    private AddMemorandumPresenter addMemorandumPresenter;
    private SelectAlertDialog selectTimeDialog;
    private TextView hour;
    private TextView minute;
    private PageRecyclerView hourRecyclerView;
    private PageRecyclerView minuteRecyclerView;
    private MinuteAdapter minuteAdapter;
    private HourAdapter hourAdapter;
    private TextView cancel;
    private TextView confirm;
    private String hourString = "";
    private String minuteString = "";
    private String startTimeString = "";
    private String endTimeString = "";
    private boolean startOrEnd;
    private List<String> hourList;
    private List<String> minuteList;

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
        hourList = new ArrayList<String>();
        minuteList = new ArrayList<String>();
        minuteAdapter = new MinuteAdapter();
        hourAdapter = new HourAdapter();
        addMemorandumPresenter = new AddMemorandumPresenter(getApplicationContext(), this);
        addMemorandumPresenter.getHourDatas();
        addMemorandumPresenter.getMinuteDatas();
        setTitleData();
        initEvent();
    }

    private void setTitleData() {
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText(R.string.save_button);
        rightMenu.setTextSize(getResources().getDimension(R.dimen.level_two_font_size));
    }

    @Override
    public void setHourData(List<String> dataList) {
        hourList = dataList;
        hourAdapter.setDatas(hourList);
        hourRecyclerView.setAdapter(hourAdapter);
    }

    @Override
    public void setMinuteData(List<String> dataList) {
        minuteList = dataList;
        minuteAdapter.setDatas(minuteList);
        minuteRecyclerView.setAdapter(minuteAdapter);
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
        Float heightProportion = Float.valueOf(getString(R.string.add_memorandum_activity_dialog_height));
        Float widthProportion = Float.valueOf(getString(R.string.add_memorandum_activity_dialog_width));
        attributes.height = (int) (Utils.getScreenHeight(DRApplication.getInstance()) * heightProportion);
        attributes.width = (int) (Utils.getScreenWidth(DRApplication.getInstance()) * widthProportion);
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
        addMemorandumPresenter.insertMemorandum(bean);
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
