package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.widget.FrameLayout;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.TabMenuAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuData;
import com.onyx.android.dr.event.ArticlePushMenuEvent;
import com.onyx.android.dr.event.DictMenuEvent;
import com.onyx.android.dr.event.GradedBooksEvent;
import com.onyx.android.dr.event.MyBooksMenuEvent;
import com.onyx.android.dr.event.NotesMenuEvent;
import com.onyx.android.dr.event.ProfessionalMaterialsMenuEvent;
import com.onyx.android.dr.event.RealTimeBooksMenuEvent;
import com.onyx.android.dr.event.SchoolBasedMaterialsMenuEvent;
import com.onyx.android.dr.event.SettingsMenuEvent;
import com.onyx.android.dr.event.TeachingAidsMenuEvent;
import com.onyx.android.dr.presenter.MainPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements MainView {

    @Bind(R.id.main_view_container)
    FrameLayout mainViewContainer;
    @Bind(R.id.tab_menu)
    PageRecyclerView tabMenu;
    private MainPresenter mainPresenter;
    private List<Library> libraryList;
    private TabMenuAdapter tabMenuAdapter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        tabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        tabMenu.addItemDecoration(dividerItemDecoration);
        tabMenuAdapter = new TabMenuAdapter();
        tabMenu.setAdapter(tabMenuAdapter);
    }

    @Override
    protected void initData() {
        mainPresenter = new MainPresenter(this);
        mainPresenter.loadData(this);
        mainPresenter.loadTabMenu(Constants.ACCOUNT_TYPE_HIGH_SCHOOL);
    }

    @Override
    public void setTabMenuData(List<MenuData> menuData) {
        tabMenuAdapter.setMenuDatas(menuData);
    }

    private String getLibraryParentId() {
        return DRPreferenceManager.loadLibraryParentId(this, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGradedBooksEvent(GradedBooksEvent event) {
        CommonNotices.showMessage(this, getString(R.string.graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyBooksMenuEvent(MyBooksMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.my_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRealTimeBooksMenuEvent(RealTimeBooksMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.real_time_articles));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSchoolBasedMaterialsMenuEvent(SchoolBasedMaterialsMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.school_based_materials));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfessionalMaterialsMenuEvent(ProfessionalMaterialsMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.professional_materials));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDictMenuEvent(DictMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.dict));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotesMenuEvent(NotesMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.notes));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTeachingAidsMenuEvent(TeachingAidsMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.teaching_aids));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingsMenuEvent(SettingsMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.settings));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticlePushMenuEvent(ArticlePushMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.article_push));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
