package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.MyCreationAdapter;
import com.onyx.android.dr.adapter.MyThinkAdapter;
import com.onyx.android.dr.adapter.MyTracksAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.dr.event.GoodSentenceNotebookEvent;
import com.onyx.android.dr.event.InfromalEssayEvent;
import com.onyx.android.dr.event.MemorandumEvent;
import com.onyx.android.dr.event.NewWordNotebookEvent;
import com.onyx.android.dr.event.PostilEvent;
import com.onyx.android.dr.event.ReadSummaryEvent;
import com.onyx.android.dr.event.ReaderResponseEvent;
import com.onyx.android.dr.event.ReadingRateEvent;
import com.onyx.android.dr.event.SketchEvent;
import com.onyx.android.dr.interfaces.MyNotesView;
import com.onyx.android.dr.presenter.MyNotesPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.DeviceUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

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
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
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
        initRecyclerView();
    }

    private void initRecyclerView() {
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
        myNotesPresenter.getImpressionsList();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.my_note);
        title.setText(getString(R.string.menu_notes));
    }

    @Override
    public void setMyracksData(List<MenuBean> menuDatas) {
        myTracksAdapter.setMenuDataList(menuDatas);
        tracksRecyclerView.setAdapter(myTracksAdapter);
    }

    @Override
    public void setMyThinkData(List<MenuBean> menuDatas) {
        myThinkAdapter.setMenuDataList(menuDatas);
        thinkRecyclerView.setAdapter(myThinkAdapter);
    }

    @Override
    public void setMyCreationData(List<MenuBean> menuDatas) {
        myCreationAdapter.setMenuDataList(menuDatas);
        creationRecyclerView.setAdapter(myCreationAdapter);
    }

    public void initEvent() {
    }

    @OnClick({R.id.menu_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewWordNotebookEvent(NewWordNotebookEvent event) {
        ActivityManager.startNewWordNotebookActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoodSentenceNoteEntity(GoodSentenceNotebookEvent event) {
        ActivityManager.startGoodSentenceNotebookActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInfromalEssayEvent(InfromalEssayEvent event) {
        ActivityManager.startInformalEssayActivity(this, Constants.MY_NOTE_TO_INFORMAL_ESSAY);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMemorandumEvent(MemorandumEvent event) {
        ActivityManager.startMemorandumActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadSummaryEvent(ReadSummaryEvent event) {
        ActivityManager.startSummaryListActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadingRateEvent(ReadingRateEvent event) {
        ActivityManager.startReadingRateActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSketchEvent(SketchEvent event) {
        ActivityManager.startNoteApp(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderResponseEvent(ReaderResponseEvent event) {
        ActivityManager.startReadingReportListActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostilEvent(PostilEvent event) {
        ActivityManager.startAnnotationListActivity(this);
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

    @Override
    protected void onResume() {
        DeviceUtils.setFullScreenOnResume(this, false);
        super.onResume();
    }
}
