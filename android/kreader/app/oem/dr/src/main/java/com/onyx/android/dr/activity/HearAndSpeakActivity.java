package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.HearAndSpeakAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.dr.event.ArticleRepeatAfterEvent;
import com.onyx.android.dr.event.SpeechRecordingEvent;
import com.onyx.android.dr.interfaces.HearAndSpeakView;
import com.onyx.android.dr.presenter.HearAndSpeakPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/7/31.
 */
public class HearAndSpeakActivity extends BaseActivity implements HearAndSpeakView {
    @Bind(R.id.hear_and_speak_activity_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    private HearAndSpeakAdapter hearAndSpeakAdapter;
    private HearAndSpeakPresenter hearAndSpeakPresenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_hear_and_speak;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        hearAndSpeakAdapter = new HearAndSpeakAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        hearAndSpeakPresenter = new HearAndSpeakPresenter(getApplicationContext(), this);
        hearAndSpeakPresenter.loadData(this);
        hearAndSpeakPresenter.loadHearAndSpeakData(Constants.ACCOUNT_HEAR_AND_SPEAK);
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.listen_and_speak);
        image.invalidate();
        title.setText(getString(R.string.menu_listen_and_say));
    }

    @Override
    public void setHearAndSpeakData(List<MenuBean> menuDatas) {
        hearAndSpeakAdapter.setMenuDataList(menuDatas);
        recyclerView.setAdapter(hearAndSpeakAdapter);
    }

    public void initEvent() {
    }

    @OnClick({R.id.image_view_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticleRepeatAfterEvent(ArticleRepeatAfterEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechRecordingEvent(SpeechRecordingEvent event) {
        ActivityManager.startRecordTimeSettingActivity(this);
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
