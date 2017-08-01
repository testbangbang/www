package com.onyx.android.dr.activity;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.HearAndSpeakAdapter;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.dr.event.ArticleRepeatAfterEvent;
import com.onyx.android.dr.event.PronounceEvaluationEvent;
import com.onyx.android.dr.event.SpeechRecordingEvent;
import com.onyx.android.dr.interfaces.HearAndSpeakView;
import com.onyx.android.dr.presenter.HearAndSpeakPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;

/**
 * Created by zhouzhiming on 2017/7/31.
 */
public class HearAndSpeakActivity extends BaseActivity implements HearAndSpeakView {
    @Bind(R.id.hear_and_speak_activity_view)
    PageRecyclerView recyclerView;
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
        initRecylcerView();
    }

    private void initRecylcerView() {
        hearAndSpeakAdapter = new HearAndSpeakAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        hearAndSpeakPresenter = new HearAndSpeakPresenter(getApplicationContext(), this);
        hearAndSpeakPresenter.loadData(this);
        hearAndSpeakPresenter.loadHearAndSpeakData(Constants.ACCOUNT_HEAR_AND_SPEAK);
        initEvent();
    }

    @Override
    public void setHearAndSpeakData(List<MenuBean> menuDatas) {
        hearAndSpeakAdapter.setMenuDataList(menuDatas);
        recyclerView.setAdapter(hearAndSpeakAdapter);
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticleRepeatAfterEvent(ArticleRepeatAfterEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechRecordingEvent(SpeechRecordingEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPronounceEvaluationEvent(PronounceEvaluationEvent event) {
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
