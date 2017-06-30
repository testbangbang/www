package com.onyx.android.dr.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.util.SparseArray;
import android.widget.FrameLayout;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.TabMenuAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuData;
import com.onyx.android.dr.event.ArticlePushMenuEvent;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.event.DictMenuEvent;
import com.onyx.android.dr.event.GradedBooksEvent;
import com.onyx.android.dr.event.MyBooksMenuEvent;
import com.onyx.android.dr.event.NotesMenuEvent;
import com.onyx.android.dr.event.ProfessionalMaterialsMenuEvent;
import com.onyx.android.dr.event.RealTimeBooksMenuEvent;
import com.onyx.android.dr.event.SchoolBasedMaterialsMenuEvent;
import com.onyx.android.dr.event.SettingsMenuEvent;
import com.onyx.android.dr.event.TeachingAidsMenuEvent;
import com.onyx.android.dr.fragment.BaseFragment;
import com.onyx.android.dr.fragment.ChildViewID;
import com.onyx.android.dr.fragment.MainViewFragment;
import com.onyx.android.dr.fragment.RealTimeBooksFragment;
import com.onyx.android.dr.fragment.SchoolBasedMaterialsFragment;
import com.onyx.android.dr.presenter.MainPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

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
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private int currentPageID;
    private SparseArray<BaseFragment> childViewList = new SparseArray<>();

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        initTabMenu();
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN_VIEW);
    }

    private void initTabMenu() {
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
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyBooksMenuEvent(MyBooksMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_my_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRealTimeBooksMenuEvent(RealTimeBooksMenuEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_REAL_TIME_BOOKS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSchoolBasedMaterialsMenuEvent(SchoolBasedMaterialsMenuEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_SCHOOL_BASED_MATERIALS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfessionalMaterialsMenuEvent(ProfessionalMaterialsMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_professional_materials));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDictMenuEvent(DictMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_dict));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotesMenuEvent(NotesMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_notes));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTeachingAidsMenuEvent(TeachingAidsMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.teaching_aids));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingsMenuEvent(SettingsMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_settings));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticlePushMenuEvent(ArticlePushMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_article_push));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToMainViewEvent(BackToMainViewEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN_VIEW);
    }

    public void switchCurrentFragment(int pageID) {
        if (currentPageID == pageID) {
            return;
        }
        if (fragmentManager == null) {
            fragmentManager = getFragmentManager();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (currentFragment != null && currentFragment.isVisible()) {
            transaction.hide(currentFragment);
        }
        BaseFragment baseFragment = getPageView(pageID);
        if (baseFragment.isStored) {
            transaction.show(baseFragment);
        } else {
            transaction.add(R.id.main_view_container, baseFragment);
            childViewList.put(pageID, baseFragment);
        }
        currentFragment = baseFragment;
        currentPageID = pageID;
        transaction.commitAllowingStateLoss();
    }

    private BaseFragment getPageView(int pageID) {
        BaseFragment baseFragment = childViewList.get(pageID);
        if (baseFragment == null) {
            switch (pageID) {
                case ChildViewID.FRAGMENT_MAIN_VIEW:
                    baseFragment = new MainViewFragment();
                    break;
                case ChildViewID.FRAGMENT_REAL_TIME_BOOKS:
                    baseFragment = new RealTimeBooksFragment();
                    break;
                case ChildViewID.FRAGMENT_SCHOOL_BASED_MATERIALS:
                    baseFragment = new SchoolBasedMaterialsFragment();
                    break;
            }
        } else {
            baseFragment.isStored = true;
        }
        return baseFragment;
    }
}
