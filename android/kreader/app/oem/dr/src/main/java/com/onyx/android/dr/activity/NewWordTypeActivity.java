package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceTypeAdapter;
import com.onyx.android.dr.bean.GoodSentenceTypeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ChineseNewWordEvent;
import com.onyx.android.dr.event.EnglishNewWordEvent;
import com.onyx.android.dr.event.MinorityLanguageNewWordEvent;
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
public class NewWordTypeActivity extends BaseActivity implements GoodSentenceTpyeView {
    @Bind(R.id.new_word_activity_type_recyclerview)
    PageRecyclerView typeRecyclerView;
    private DividerItemDecoration dividerItemDecoration;
    private GoodSentenceTypeAdapter goodSentenceTypeAdapter;
    private GoodSentenceTypePresenter goodSentenceTypePresenter;
    private String englishUsed = "";
    private String chineseUsed = "";
    private String otherLanguageUsed = "";

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_new_word_type;
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
        initNewWordData();
        initEvent();
    }

    private void initNewWordData() {
        goodSentenceTypePresenter = new GoodSentenceTypePresenter(getApplicationContext(), this);
        goodSentenceTypePresenter.loadNewWordData(englishUsed, chineseUsed, otherLanguageUsed);
        goodSentenceTypePresenter.loadDataByType(Constants.ACCOUNT_TYPE_NEW_WORD);
    }

    @Override
    public void setGoodSentenceTpyeData(List<GoodSentenceTypeBean> dataList) {
        goodSentenceTypeAdapter.setMenuDataList(dataList);
        typeRecyclerView.setAdapter(goodSentenceTypeAdapter);
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishNewWordEvent(EnglishNewWordEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseNewWordEvent(ChineseNewWordEvent event) {
        ActivityManager.startNewWordNotebookActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMinorityLanguageNewWordEvent(MinorityLanguageNewWordEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
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