package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceTypeAdapter;
import com.onyx.android.dr.bean.GoodSentenceTypeBean;
import com.onyx.android.dr.common.ActivityManager;
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
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class NewWordTypeActivity extends BaseActivity implements GoodSentenceTpyeView {
    @Bind(R.id.new_word_activity_type_recyclerview)
    PageRecyclerView typeRecyclerView;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
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
        initTitleData();
        initNewWordData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.new_word_notebook);
        title.setText(getString(R.string.new_word_notebook));
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

    @OnClick({R.id.image_view_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishNewWordEvent(EnglishNewWordEvent event) {
        ActivityManager.startNewWordNotebookActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseNewWordEvent(ChineseNewWordEvent event) {
        ActivityManager.startNewWordNotebookActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMinorityLanguageNewWordEvent(MinorityLanguageNewWordEvent event) {
        ActivityManager.startNewWordNotebookActivity(this);
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
