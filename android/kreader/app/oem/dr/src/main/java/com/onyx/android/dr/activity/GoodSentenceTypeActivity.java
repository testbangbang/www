package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceTypeAdapter;
import com.onyx.android.dr.bean.GoodSentenceTypeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.event.ChineseGoodSentenceEvent;
import com.onyx.android.dr.event.EnglishGoodSentenceEvent;
import com.onyx.android.dr.event.MinorityLanguageGoodSentenceEvent;
import com.onyx.android.dr.interfaces.GoodSentenceTpyeView;
import com.onyx.android.dr.presenter.GoodSentenceTypePresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class GoodSentenceTypeActivity extends BaseActivity implements GoodSentenceTpyeView {
    @Bind(R.id.good_sentence_activity_type_recyclerview)
    PageRecyclerView typeRecyclerView;
    private DividerItemDecoration dividerItemDecoration;
    private GoodSentenceTypeAdapter goodSentenceTypeAdapter;
    private GoodSentenceTypePresenter goodSentenceTypePresenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_good_sentence_type;
    }

    @Override
    protected void initConfig() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
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
        goodSentenceTypePresenter = new GoodSentenceTypePresenter(this);
        goodSentenceTypePresenter.loadData(this);
        goodSentenceTypePresenter.loadGoodSentenceType();
        initEvent();
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
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseGoodSentenceEvent(ChineseGoodSentenceEvent event) {
        ActivityManager.startGoodSentenceNotebookActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMinorityLanguageGoodSentenceEvent(MinorityLanguageGoodSentenceEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
