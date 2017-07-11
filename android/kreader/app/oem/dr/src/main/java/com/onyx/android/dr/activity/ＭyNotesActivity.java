package com.onyx.android.dr.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.MyNotesAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuData;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.FrenchQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;
import com.onyx.android.dr.interfaces.MyNotesView;
import com.onyx.android.dr.presenter.MyNotesPresenter;
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
public class ＭyNotesActivity extends BaseActivity implements MyNotesView{
    @Bind(R.id.my_notes_activity_tracks_recyclerview)
    PageRecyclerView tracksRecyclerView;
    @Bind(R.id.my_notes_activity_think_recyclerview)
    PageRecyclerView thinkRecyclerView;
    @Bind(R.id.my_notes_activity_creation_recyclerview)
    PageRecyclerView creationRecyclerView;
    private DividerItemDecoration dividerItemDecoration;
    private MyNotesAdapter myNotesAdapter;
    private MyNotesPresenter myNotesPresenter;

    public static void startＭyNotesActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, ＭyNotesActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_my_notes;
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
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        tracksRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        tracksRecyclerView.addItemDecoration(dividerItemDecoration);
        myNotesAdapter = new MyNotesAdapter();
        tracksRecyclerView.setAdapter(myNotesAdapter);
    }

    @Override
    protected void initData() {
        myNotesPresenter = new MyNotesPresenter(this);
        myNotesPresenter.loadData(this);
        myNotesPresenter.loadMyNotesType(Constants.ACCOUNT_TYPE_MY_TRACKS);
        myNotesPresenter.loadMyNotesType(Constants.ACCOUNT_TYPE_MY_THINK);
        myNotesPresenter.loadMyNotesType(Constants.ACCOUNT_TYPE_MY_CREATION);
        initEvent();
    }

    @Override
    public void setMyNotesTypeData(List<MenuData> menuDatas) {
        myNotesAdapter.setMenuDataList(menuDatas);
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishQueryEvent(EnglishQueryEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseQueryEvent(ChineseQueryEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJapaneseQueryEvent(JapaneseQueryEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFrenchQueryEvent(FrenchQueryEvent event) {
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
