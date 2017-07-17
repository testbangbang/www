package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.event.EnglishGoodSentenceEvent;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.presenter.NewWordPresenter;
import com.onyx.android.dr.util.EventBusUtils;
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
public class NewWordNotebookActivity extends BaseActivity implements NewWordView {
    @Bind(R.id.new_word_activity_recyclerview)
    PageRecyclerView goodSentenceRecyclerView;
    @Bind(R.id.new_word_activity_month_spinner)
    Spinner monthSpinner;
    @Bind(R.id.new_word_activity_week_spinner)
    Spinner weekSpinner;
    @Bind(R.id.new_word_activity_day_spinner)
    Spinner daySpinner;
    private DividerItemDecoration dividerItemDecoration;
    private NewWordAdapter newWordAdapter;
    private NewWordPresenter newWordPresenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_new_word_notebook;
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
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        newWordAdapter = new NewWordAdapter();
        goodSentenceRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        goodSentenceRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        newWordPresenter = new NewWordPresenter(getApplicationContext(), this);
        newWordPresenter.getAllNewWordData();

        initSpinnerDatas();
        initEvent();
    }

    private void initSpinnerDatas() {
        String[] monthDatas = getResources().getStringArray(R.array.month);
        ArrayAdapter<String> monthAdapter=new ArrayAdapter<String>(this, R.layout.item_spinner_unexpanded_pattern, monthDatas);
        monthAdapter.setDropDownViewResource(R.layout.item_spinner_expanded_pattern);
        monthSpinner .setAdapter(monthAdapter);

        String[] weekDatas = getResources().getStringArray(R.array.week);
        ArrayAdapter<String> weekAdapter=new ArrayAdapter<String>(this,  R.layout.item_spinner_unexpanded_pattern, weekDatas);
        weekAdapter.setDropDownViewResource(R.layout.item_spinner_expanded_pattern);
        weekSpinner .setAdapter(weekAdapter);

        String[] dayDatas = getResources().getStringArray(R.array.day);
        ArrayAdapter<String> dayAdapter=new ArrayAdapter<String>(this,  R.layout.item_spinner_unexpanded_pattern, dayDatas);
        dayAdapter.setDropDownViewResource(R.layout.item_spinner_expanded_pattern);
        daySpinner .setAdapter(dayAdapter);
    }

    @Override
    public void setNewWordData(List<NewWordNoteBookEntity> dataList) {
        newWordAdapter.setDataList(dataList);
        goodSentenceRecyclerView.setAdapter(newWordAdapter);
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishGoodSentenceEvent(EnglishGoodSentenceEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusUtils.registerEventBus(this);
        ButterKnife.bind(this);
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
