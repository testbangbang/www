package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.MemorandumBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.event.HourEvent;
import com.onyx.android.dr.interfaces.AddMemorandumView;
import com.onyx.android.dr.presenter.AddMemorandumPresenter;
import com.onyx.android.dr.util.DictPreference;
import com.onyx.android.dr.view.DefaultEditText;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/7/21.
 */
public class AddMemorandumActivity extends BaseActivity implements AddMemorandumView, TimePickerDialog.TimePickerDialogInterface {
    @Bind(R.id.add_memorandum_activity_start_time)
    TextView time;
    @Bind(R.id.title_bar_right_menu)
    TextView rightMenu;
    @Bind(R.id.add_memorandum_activity_content)
    DefaultEditText contentEditText;
    private AddMemorandumPresenter addMemorandumPresenter;
    private TimePickerDialog timePickerDialog;
    private String timeHorizon;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_add_memorandum;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        DictPreference.init(this);
        loadMemorandumData();
        setTitleData();
        initEvent();
    }

    private void loadMemorandumData() {
        addMemorandumPresenter = new AddMemorandumPresenter(getApplicationContext(), this);
        timePickerDialog = new TimePickerDialog(this);
    }

    private void setTitleData() {
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText(R.string.save_button);
        rightMenu.setTextSize(getResources().getDimension(R.dimen.level_two_font_size));
    }

    @Override
    public void setHourData(List<String> dataList) {
    }

    @Override
    public void setMinuteData(List<String> dataList) {
    }

    private void initEvent() {
    }

    @OnClick({R.id.add_memorandum_activity_start_time,
            R.id.image_view_back,
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
                timePickerDialog.showTimePickerDialog();
                break;
        }
    }

    @Override
    public void positiveListener() {
        timeHorizon = timePickerDialog.getTimeHorizon();
        EventBus.getDefault().post(new HourEvent());
    }

    private void insertData() {
        if (StringUtils.isNullOrEmpty(timeHorizon)) {
            CommonNotices.showMessage(this, getString(R.string.select_time));
            return;
        }
        String content = contentEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(content)) {
            CommonNotices.showMessage(this, getString(R.string.input_memorandum));
            return;
        }
        MemorandumBean bean = new MemorandumBean();
        bean.setTimeQuantum(timeHorizon);
        bean.setMatter(content);
        addMemorandumPresenter.insertMemorandum(bean);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHourEvent(HourEvent event) {
        if (StringUtils.isNullOrEmpty(timeHorizon)) {
            time.setText(getString(R.string.select_time));
        } else {
            time.setText(timeHorizon);
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
