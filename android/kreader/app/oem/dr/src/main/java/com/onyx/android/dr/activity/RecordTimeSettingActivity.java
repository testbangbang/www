package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SpeechTimeAdapter;
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
    private List<String> speechTimeData;
    private String speechTime = "";

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
    public void setRecordTime(List<String> dataList) {
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
                String content = speechTimeData.get(position);
                speechTime = content.substring(0, 1);
                int minute = Integer.valueOf(speechTime);
                DictPreference.setIntValue(RecordTimeSettingActivity.this, Constants.SPEECH_TIME, minute);
                EventBus.getDefault().post(new SelfDefinedTimeEvent());
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
            if (minute <= 0 || minute > 15) {
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
