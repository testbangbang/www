package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceTypeAdapter;
import com.onyx.android.dr.bean.GoodSentenceTypeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ChineseGoodSentenceEvent;
import com.onyx.android.dr.event.EnglishGoodSentenceEvent;
import com.onyx.android.dr.event.MinorityLanguageGoodSentenceEvent;
import com.onyx.android.dr.interfaces.GoodSentenceTpyeView;
import com.onyx.android.dr.presenter.GoodSentenceTypePresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class GoodSentenceTypeActivity extends BaseActivity implements GoodSentenceTpyeView {
    @Bind(R.id.good_sentence_activity_type_recyclerview)
    PageRecyclerView typeRecyclerView;
    private DividerItemDecoration dividerItemDecoration;
    private GoodSentenceTypeAdapter goodSentenceTypeAdapter;
    private GoodSentenceTypePresenter goodSentenceTypePresenter;
    private String englishUsed = "";
    private String chineseUsed = "";
    private String otherLanguageUsed = "";

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_good_sentence_type;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecylcerView();
    }

    private void initRecylcerView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.HORIZONTAL);
        goodSentenceTypeAdapter = new GoodSentenceTypeAdapter();
        typeRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        typeRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        initGoodSentenceData();
        initEvent();
    }

    private void initGoodSentenceData() {
        goodSentenceTypePresenter = new GoodSentenceTypePresenter(getApplicationContext(), this);
        goodSentenceTypePresenter.loadGoodSentenceData(englishUsed, chineseUsed, otherLanguageUsed);
        goodSentenceTypePresenter.loadDataByType(Constants.ACCOUNT_TYPE_GOOD_SENTENCE);
    }

    @Override
    public void setGoodSentenceTpyeData(List<GoodSentenceTypeBean> dataList) {
        goodSentenceTypeAdapter.setMenuDataList(dataList);
        typeRecyclerView.setAdapter(goodSentenceTypeAdapter);
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishGoodSentenceEvent(EnglishGoodSentenceEvent event) {
        ActivityManager.startGoodSentenceNotebookActivity(this, Constants.ENGLISH_NEW_WORD_NOTEBOOK);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseGoodSentenceEvent(ChineseGoodSentenceEvent event) {
        ActivityManager.startGoodSentenceNotebookActivity(this, Constants.CHINESE_NEW_WORD_NOTEBOOK);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMinorityLanguageGoodSentenceEvent(MinorityLanguageGoodSentenceEvent event) {
        ActivityManager.startGoodSentenceNotebookActivity(this, Constants.JAPANESE_NEW_WORD_NOTEBOOK);
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
