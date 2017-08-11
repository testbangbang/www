package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.util.DictPreference;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/7/31.
 */
public class SpeechRecordingActivity extends BaseActivity {
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.speech_recording_activity_content)
    TextView content;
    @Bind(R.id.speech_recording_activity_start_lecture)
    TextView startLecture;
    @Bind(R.id.speech_recording_activity_record_playback)
    TextView recordPlayback;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_speech_recording;
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
        getIntentData();
        initTitleData();
        initEvent();
    }

    private void getIntentData() {
        String informalEssayContent = getIntent().getStringExtra(Constants.INFORMAL_ESSAY_CONTENT);
        content.setText(informalEssayContent);
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.speech_recording);
        image.invalidate();
        title.setText(getString(R.string.speech_recording));
    }

    public void initEvent() {
    }

    @OnClick({R.id.image_view_back,
            R.id.speech_recording_activity_start_lecture})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
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
