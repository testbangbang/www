package com.onyx.android.dr.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SpeechTimeAdapter;
import com.onyx.android.dr.bean.SpeechTimeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.SelfDefinedTimeEvent;
import com.onyx.android.dr.interfaces.RecordTimeSettingView;
import com.onyx.android.dr.presenter.RecordTimeSettingPresenter;
import com.onyx.android.dr.util.DictPreference;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
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
 * Created by zhouzhiming on 2017/7/31.
 */
public class RecordTimeSettingActivity extends BaseActivity implements RecordTimeSettingView {
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.record_time_activity_next_step)
    TextView nextStep;
    @Bind(R.id.record_time_activity_self_defined_time)
    EditText selfDefinedTime;
    @Bind(R.id.record_time_activity_view)
    PageRecyclerView recyclerView;
    private SpeechTimeAdapter speechTimeAdapter;
    private RecordTimeSettingPresenter recordTimeSettingPresenter;
    private List<SpeechTimeBean> speechTimeData;
    private String speechTime = "";
    private int MIN_NUMBER = 0;
    private int DEFAULT_NUMBER = -1;
    private int MAX_NUMBER = 15;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_record_time_setting;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        speechTimeAdapter = new SpeechTimeAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        DictPreference.init(this);
        speechTimeData = new ArrayList<>();
        recordTimeSettingPresenter = new RecordTimeSettingPresenter(this);
        recordTimeSettingPresenter.getRecordTimeData();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.speech_recording);
        image.invalidate();
        title.setText(getString(R.string.speech_recording));
    }

    @Override
    public void setRecordTime(List<SpeechTimeBean> dataList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        speechTimeData.addAll(dataList);
        speechTimeAdapter.setMenuDataList(speechTimeData);
        recyclerView.setAdapter(speechTimeAdapter);
    }

    public void initEvent() {
        speechTimeAdapter.setOnItemClick(new SpeechTimeAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int minute = speechTimeData.get(position).getNumber();
                speechTime = String.valueOf(minute);
                DictPreference.setIntValue(RecordTimeSettingActivity.this, Constants.SPEECH_TIME, minute);
                EventBus.getDefault().post(new SelfDefinedTimeEvent());
            }
        });

        selfDefinedTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtils.isNullOrEmpty(s.toString())) {
                    speechTimeAdapter.selectedPosition = DEFAULT_NUMBER;
                    speechTime = "";
                    speechTimeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick({R.id.image_view_back,
            R.id.record_time_activity_next_step})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.record_time_activity_next_step:
                saveSpeechTime();
                break;
        }
    }

    private void saveSpeechTime() {
        String time = selfDefinedTime.getText().toString();
        if (StringUtils.isNullOrEmpty(speechTime) && StringUtils.isNullOrEmpty(time)) {
            CommonNotices.showMessage(RecordTimeSettingActivity.this, getString(R.string.set_speech_time));
            return;
        }
        if (!StringUtils.isNullOrEmpty(time)) {
            int minute = Integer.valueOf(time);
            if (minute <= MIN_NUMBER || minute > MAX_NUMBER) {
                CommonNotices.showMessage(RecordTimeSettingActivity.this, getString(R.string.record_time_activity_edit_text_hint));
                return;
            }
            DictPreference.setIntValue(RecordTimeSettingActivity.this, Constants.SPEECH_TIME, minute);
        }
        ActivityManager.startInformalEssayActivity(this, Constants.RECORD_TIME_SETTING_TO_INFORMAL_ESSAY);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelfDefinedTimeEvent(SelfDefinedTimeEvent event) {
        selfDefinedTime.setText("");
        selfDefinedTime.setHint(getString(R.string.record_time_activity_edit_text_hint));
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
