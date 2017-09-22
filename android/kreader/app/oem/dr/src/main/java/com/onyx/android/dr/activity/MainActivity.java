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
import com.onyx.android.dr.data.MainTabMenuConfig;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.dr.event.AccountAvailableEvent;
import com.onyx.android.dr.event.ApkDownloadSucceedEvent;
import com.onyx.android.dr.event.ApplicationEvent;
import com.onyx.android.dr.event.ArticlePushMenuEvent;
import com.onyx.android.dr.event.BackToBookshelfEvent;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.event.BookshelfMenuEvent;
import com.onyx.android.dr.event.BookstoreMenuEvent;
import com.onyx.android.dr.event.DictMenuEvent;
import com.onyx.android.dr.event.DownloadSucceedEvent;
import com.onyx.android.dr.event.HaveNewVersionApkEvent;
import com.onyx.android.dr.event.HaveNewVersionEvent;
import com.onyx.android.dr.event.ListenAndSayMenuEvent;
import com.onyx.android.dr.event.LoginFailedEvent;
import com.onyx.android.dr.event.MoreBooksEvent;
import com.onyx.android.dr.event.NotesMenuEvent;
import com.onyx.android.dr.event.SettingsMenuEvent;
import com.onyx.android.dr.event.ToBookshelfV2Event;
import com.onyx.android.dr.event.UpdateDownloadSucceedEvent;
import com.onyx.android.dr.event.WifiConnectedEvent;
import com.onyx.android.dr.fragment.BaseFragment;
import com.onyx.android.dr.fragment.BookshelfFragment;
import com.onyx.android.dr.fragment.BookshelfV2Fragment;
import com.onyx.android.dr.fragment.ChildViewID;
import com.onyx.android.dr.fragment.MainViewFragment;
import com.onyx.android.dr.fragment.MoreBooksFragment;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.presenter.MainPresenter;
import com.onyx.android.dr.util.ApkUtils;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.util.SystemUtils;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

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
    private LibraryDataHolder dataHolder;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {
        mainPresenter = new MainPresenter(this);
        MainTabMenuConfig.loadMenuInfo(this);
        mainPresenter.getMyGroup();
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
        mainPresenter.loadTabMenu(DRPreferenceManager.getUserType(DRApplication.getInstance(), ""));
        autoLogin();
    }

    private void autoLogin() {
        if (DRPreferenceManager.getAutoLogin(this, false)) {
            mainPresenter.authToken(this);
        }
    }

    @Override
    public void setTabMenuData(List<MenuBean> menuData) {
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN_VIEW);
        tabMenuAdapter.setMenuDataList(menuData);
        tabMenuAdapter.notifyDataSetChanged();
        tabMenuNext.setVisibility(menuData.size() > tabMenuAdapter.getColumnCount() ? View.VISIBLE : View.GONE);
    }

    private String getLibraryParentId() {
        return DRPreferenceManager.loadLibraryParentId(this, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDictMenuEvent(DictMenuEvent event) {
        ActivityManager.startDictQueryActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotesMenuEvent(NotesMenuEvent event) {
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDownloadSucceedEvent(DownloadSucceedEvent event) {
        getDataHolder().getCloudManager().getCloudDataProvider().saveMetadata(DRApplication.getInstance(), event.getMetadata());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookshelfMenuEvent(BookshelfMenuEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_BOOKSHELF);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookstoreMenuEvent(BookstoreMenuEvent event) {
        ActivityManager.startEBookStoreActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoreBooksEvent(MoreBooksEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_COMMON_BOOKS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHaveNewVersionEvent(HaveNewVersionEvent event) {
        FirmwareUpdateRequest request = event.getRequest();
        Firmware resultFirmware = request.getResultFirmware();
        String changeLog = resultFirmware.getChangeLog();
        if (StringUtils.isNullOrEmpty(changeLog)) {
            changeLog = resultFirmware.buildDisplayId;
        }
        String downloadUrl = resultFirmware.getUrl();
        if (StringUtils.isNotBlank(downloadUrl)) {
            ApkUtils.showLocalCheckDialog(DRApplication.getInstance(), changeLog, downloadUrl);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHaveNewVersionApkEvent(HaveNewVersionApkEvent event) {
        ApplicationUpdate applicationUpdate = event.getApplicationUpdate();
        Map<String, List<String>> changeLogs = applicationUpdate.changeLogs;
        String[] downloadUrlList = applicationUpdate.downloadUrlList;
        String language = getResources().getConfiguration().locale.toString();
        String message = String.format(getString(R.string.current_version), SystemUtils.getAPPVersionCode(MainActivity.this)) + "--->";
        message += String.format(getString(R.string.update_version), applicationUpdate.versionCode) + "\n";
        message += getString(R.string.update_content);
        if (changeLogs != null && changeLogs.size() > 0) {
            List<String> messageList = changeLogs.get(language);
            for (int i = 0; i < messageList.size(); i++) {
                message += messageList.get(i);
                message += "\n";
            }
        }
        ApkUtils.showNewApkDialog(DRApplication.getInstance(), message, downloadUrlList[0]);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApkDownloadSucceedEvent(ApkDownloadSucceedEvent event) {
        ApkUtils.installApk(DRApplication.getInstance(), Constants.APK_DOWNLOAD_PATH);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateDownloadSucceedEvent(UpdateDownloadSucceedEvent event) {
        ApkUtils.firmwareLocal();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiConnectedEvent(WifiConnectedEvent event) {
        autoLogin();
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
                    baseFragment = new MoreBooksFragment();
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
            return true;
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
