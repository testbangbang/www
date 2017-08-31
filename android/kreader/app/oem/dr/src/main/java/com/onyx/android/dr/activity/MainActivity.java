package com.onyx.android.dr.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.TabMenuAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.dr.event.AccountAvailableEvent;
import com.onyx.android.dr.event.ApplicationEvent;
import com.onyx.android.dr.event.ArticlePushMenuEvent;
import com.onyx.android.dr.event.BackToBookshelfEvent;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.event.BookshelfEvent;
import com.onyx.android.dr.event.DictMenuEvent;
import com.onyx.android.dr.event.DownloadSucceedEvent;
import com.onyx.android.dr.event.GradedBooksEvent;
import com.onyx.android.dr.event.ListenAndSayMenuEvent;
import com.onyx.android.dr.event.LoginFailedEvent;
import com.onyx.android.dr.event.MainLibraryTabEvent;
import com.onyx.android.dr.event.MyBooksMenuEvent;
import com.onyx.android.dr.event.NotesMenuEvent;
import com.onyx.android.dr.event.ProfessionalMaterialsMenuEvent;
import com.onyx.android.dr.event.RealTimeBooksMenuEvent;
import com.onyx.android.dr.event.SchoolBasedMaterialsMenuEvent;
import com.onyx.android.dr.event.SettingsMenuEvent;
import com.onyx.android.dr.event.ToBookshelfV2Event;
import com.onyx.android.dr.event.WifiConnectedEvent;
import com.onyx.android.dr.fragment.BaseFragment;
import com.onyx.android.dr.fragment.BookshelfFragment;
import com.onyx.android.dr.fragment.BookshelfV2Fragment;
import com.onyx.android.dr.fragment.ChildViewID;
import com.onyx.android.dr.fragment.CommonBooksFragment;
import com.onyx.android.dr.fragment.MainViewFragment;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.presenter.MainPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainView {
    @Bind(R.id.main_view_container)
    FrameLayout mainViewContainer;
    @Bind(R.id.tab_menu)
    PageRecyclerView tabMenu;
    @Bind(R.id.tab_menu_prev)
    ImageView tabMenuPrev;
    @Bind(R.id.tab_menu_next)
    ImageView tabMenuNext;
    private MainPresenter mainPresenter;
    private TabMenuAdapter tabMenuAdapter;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private int currentPageID = ChildViewID.BASE_VIEW;
    private SparseArray<BaseFragment> childViewList = new SparseArray<>();
    private List<Library> libraryList;
    private LibraryDataHolder dataHolder;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {
        mainPresenter = new MainPresenter(this);
        mainPresenter.loadData(this);
    }

    @Override
    protected void initView() {
        initTabMenu();
    }

    private void initTabMenu() {
        tabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        tabMenu.addItemDecoration(dividerItemDecoration);
        tabMenuAdapter = new TabMenuAdapter();
        tabMenu.setAdapter(tabMenuAdapter);
        tabMenu.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                setPrevAndNextButtonVisible();
            }
        });
    }

    private void setPrevAndNextButtonVisible() {
        int currentPage = tabMenu.getPaginator().getCurrentPage();
        int pages = tabMenu.getPaginator().pages();
        tabMenuPrev.setVisibility(currentPage == 0 ? View.GONE : View.VISIBLE);
        tabMenuNext.setVisibility(currentPage < pages - 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initData() {
        mainPresenter.authToken();
    }

    @Override
    public void setTabMenuData(List<MenuBean> menuData) {
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN_VIEW);
        tabMenuAdapter.setMenuDataList(menuData);
        tabMenuAdapter.notifyDataSetChanged();
        tabMenuNext.setVisibility(menuData.size() > tabMenuAdapter.getColumnCount() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLibraryList(List<Library> libraryList) {
        this.libraryList = libraryList;
    }

    private String getLibraryParentId() {
        return DRPreferenceManager.loadLibraryParentId(this, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiConnectedEvent(WifiConnectedEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGradedBooksEvent(GradedBooksEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyBooksMenuEvent(MyBooksMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRealTimeBooksMenuEvent(RealTimeBooksMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSchoolBasedMaterialsMenuEvent(SchoolBasedMaterialsMenuEvent event) {

    }

    private void switchCommonFragment(String libraryName) {
        switchCurrentFragment(ChildViewID.FRAGMENT_COMMON_BOOKS);
        CommonBooksFragment fragment = (CommonBooksFragment) getPageView(ChildViewID.FRAGMENT_COMMON_BOOKS);
        fragment.setData(getLibraryParentId(), libraryName, R.drawable.ic_real_time_books);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfessionalMaterialsMenuEvent(ProfessionalMaterialsMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDictMenuEvent(DictMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_dict));
        ActivityManager.startDictQueryActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotesMenuEvent(NotesMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_notes));
        ActivityManager.startMyNotesActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListenAndSayMenuEvent(ListenAndSayMenuEvent event) {
        ActivityManager.startHearAndSpeakActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplicationEvent(ApplicationEvent event) {
        ActivityManager.startApplicationsActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingsMenuEvent(SettingsMenuEvent event) {
        ActivityManager.startSettingActivity(this, Constants.DEVICE_SETTING_FRAGMENT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticlePushMenuEvent(ArticlePushMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_article_push));
        ActivityManager.startGroupHomePageActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToMainViewEvent(BackToMainViewEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN_VIEW);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToBookshelfV2Event(ToBookshelfV2Event event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_BOOKSHELF_V2);
        BookshelfV2Fragment fragment = (BookshelfV2Fragment) getPageView(ChildViewID.FRAGMENT_BOOKSHELF_V2);
        fragment.setData(event.getTitle(), event.getArgs());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginFailedEvent(LoginFailedEvent event) {
        ActivityManager.startLoginActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountAvailableEvent(AccountAvailableEvent event) {
        mainPresenter.getMyGroup();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToBookshelfEvent(BackToBookshelfEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_BOOKSHELF);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainLibraryTabEvent(MainLibraryTabEvent event) {
        Library library = event.getLibrary();
        CommonNotices.showMessage(this, event.getLibrary().getName());
        switchCurrentFragment(ChildViewID.FRAGMENT_BOOKSHELF);
        BookshelfFragment fragment = (BookshelfFragment) getPageView(ChildViewID.FRAGMENT_BOOKSHELF);
        fragment.setData(library);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDownloadSucceedEvent(DownloadSucceedEvent event) {
        getDataHolder().getCloudManager().getCloudDataProvider().saveMetadata(DRApplication.getInstance(), event.getMetadata());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookshelfEvent(BookshelfEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_BOOKSHELF);
        BookshelfFragment fragment = (BookshelfFragment) getPageView(ChildViewID.FRAGMENT_BOOKSHELF);
        fragment.setData(event.getLanguage());
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(this);
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
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
                case ChildViewID.FRAGMENT_COMMON_BOOKS:
                    baseFragment = new CommonBooksFragment();
                    break;
                case ChildViewID.FRAGMENT_BOOKSHELF:
                    baseFragment = new BookshelfFragment();
                    break;
                case ChildViewID.FRAGMENT_BOOKSHELF_V2:
                    baseFragment = new BookshelfV2Fragment();
                    break;
            }
        } else {
            baseFragment.isStored = true;
        }
        return baseFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        BaseFragment currentFragment = getPageView(currentPageID);
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (currentFragment != null && currentFragment.onKeyBack()) {
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_PAGE_UP) {
            if (currentFragment != null && currentFragment.onKeyPageUp()) {
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_PAGE_DOWN) {
            if (currentFragment != null && currentFragment.onKeyPageDown()) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @OnClick({R.id.tab_menu_prev, R.id.tab_menu_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_menu_prev:
                tabMenu.prevPage();
                break;
            case R.id.tab_menu_next:
                tabMenu.nextPage();
                break;
        }
    }
}
