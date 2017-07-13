package com.onyx.android.dr.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.widget.FrameLayout;

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
import com.onyx.android.dr.fragment.BaseFragment;
import com.onyx.android.dr.fragment.BookshelfFragment;
import com.onyx.android.dr.fragment.BookshelfV2Fragment;
import com.onyx.android.dr.fragment.ChildViewID;
import com.onyx.android.dr.fragment.CommonBooksFragment;
import com.onyx.android.dr.fragment.MainViewFragment;
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
    private TabMenuAdapter tabMenuAdapter;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private int currentPageID = ChildViewID.BASE_VIEW;
    private SparseArray<BaseFragment> childViewList = new SparseArray<>();
    private List<Library> libraryList;

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
        mainPresenter.loadTabMenu("");
        mainPresenter.authToken();
    }

    @Override
    public void setTabMenuData(List<MenuBean> menuData) {
        tabMenuAdapter.setMenuDataList(menuData);
        tabMenuAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLibraryList(List<Library> libraryList) {
        this.libraryList = libraryList;
    }

    private String getLibraryParentId() {
        return DRPreferenceManager.loadLibraryParentId(this, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGradedBooksEvent(GradedBooksEvent event) {
        switchCommonFragment(Constants.GRADED_BOOKS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyBooksMenuEvent(MyBooksMenuEvent event) {
        switchCommonFragment(Constants.MY_BOOKS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRealTimeBooksMenuEvent(RealTimeBooksMenuEvent event) {
        switchCommonFragment(Constants.REAL_TIME_BOOKS_LIBRARY_NAME);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSchoolBasedMaterialsMenuEvent(SchoolBasedMaterialsMenuEvent event) {
        switchCommonFragment(Constants.SCHOOL_BASED_MATERIALS_LIBRARY_NAME);
    }

    private void switchCommonFragment(String libraryName) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfessionalMaterialsMenuEvent(ProfessionalMaterialsMenuEvent event) {
        switchCommonFragment(Constants.PROFESSIONAL_MATERIALS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDictMenuEvent(DictMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_dict));
        switchCurrentFragment(ChildViewID.FRAGMENT_BOOKSHELF);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotesMenuEvent(NotesMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_notes));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListenAndSayMenuEvent(ListenAndSayMenuEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_listen_and_say));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplicationEvent(ApplicationEvent event) {
        ActivityManager.startApplicationsActivity(this);
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
        mainPresenter.lookCloudLibraryList(null);
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
        fragment.setData(library, event.getLanguages());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookshelfEvent(BookshelfEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_BOOKSHELF);
        BookshelfFragment fragment = (BookshelfFragment) getPageView(ChildViewID.FRAGMENT_BOOKSHELF);
        fragment.setData(event.getLanguage(), libraryList);
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
        mainPresenter.lookCloudLibraryList("");
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
}
