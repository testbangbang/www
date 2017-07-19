package com.onyx.android.dr.activity;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.MyCreationAdapter;
import com.onyx.android.dr.adapter.MyThinkAdapter;
import com.onyx.android.dr.adapter.MyTracksAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuData;
import com.onyx.android.dr.event.GoodSentenceNotebookEvent;
import com.onyx.android.dr.event.NewWordNotebookEvent;
import com.onyx.android.dr.interfaces.MyNotesView;
import com.onyx.android.dr.presenter.MyNotesPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MyNotesActivity extends BaseActivity implements MyNotesView {
    @Bind(R.id.my_notes_activity_tracks_recyclerview)
    PageRecyclerView tracksRecyclerView;
    @Bind(R.id.my_notes_activity_think_recyclerview)
    PageRecyclerView thinkRecyclerView;
    @Bind(R.id.my_notes_activity_creation_recyclerview)
    PageRecyclerView creationRecyclerView;
    private MyTracksAdapter myTracksAdapter;
    private MyThinkAdapter myThinkAdapter;
    private MyCreationAdapter myCreationAdapter;
    private MyNotesPresenter myNotesPresenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_my_notes;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecylcerView();
    }

    private void initRecylcerView() {
        myTracksAdapter = new MyTracksAdapter();
        myThinkAdapter = new MyThinkAdapter();
        myCreationAdapter = new MyCreationAdapter();
        tracksRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        thinkRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        creationRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        myNotesPresenter = new MyNotesPresenter(getApplicationContext(), this);
        myNotesPresenter.loadData(this);
        myNotesPresenter.loadMyTracks(Constants.ACCOUNT_TYPE_MY_TRACKS);
        myNotesPresenter.loadMyThink(Constants.ACCOUNT_TYPE_MY_THINK);
        myNotesPresenter.loadMyCreation(Constants.ACCOUNT_TYPE_MY_CREATION);
        initEvent();
    }

    @Override
    public void setMyracksData(List<MenuData> menuDatas) {
        myTracksAdapter.setMenuDataList(menuDatas);
        tracksRecyclerView.setAdapter(myTracksAdapter);
    }

    @Override
    public void setMyThinkData(List<MenuData> menuDatas) {
        myThinkAdapter.setMenuDataList(menuDatas);
        thinkRecyclerView.setAdapter(myThinkAdapter);
    }

    @Override
    public void setMyCreationData(List<MenuData> menuDatas) {
        myCreationAdapter.setMenuDataList(menuDatas);
        creationRecyclerView.setAdapter(myCreationAdapter);
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewWordNotebookEvent(NewWordNotebookEvent event) {
        ActivityManager.startNewWordTypeActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoodSentenceNoteEntity(GoodSentenceNotebookEvent event) {
        ActivityManager.startGoodSentenceTypeActivity(this);
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
        EventBus.getDefault().unregister(this);
    }
}
