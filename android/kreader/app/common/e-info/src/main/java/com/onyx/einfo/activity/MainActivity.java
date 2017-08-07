package com.onyx.einfo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.data.model.v2.GroupContainer;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.einfo.R;
import com.onyx.einfo.InfoApp;
import com.onyx.einfo.action.ActionChain;
import com.onyx.einfo.action.AuthTokenAction;
import com.onyx.einfo.action.CloudGroupContainerListLoadAction;
import com.onyx.einfo.adapter.ViewPagerAdapter;
import com.onyx.einfo.custom.NoSwipePager;
import com.onyx.einfo.events.BookLibraryEvent;
import com.onyx.einfo.events.DataRefreshEvent;
import com.onyx.einfo.events.TabSwitchEvent;
import com.onyx.einfo.listener.OnPageChangeListenerImpl;
import com.onyx.einfo.listener.OnTabSelectedListenerImpl;
import com.onyx.einfo.model.TabAction;
import com.onyx.einfo.model.TabLibrary;
import com.onyx.einfo.utils.QRCodeUtil;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/5/13.
 */
public class MainActivity extends BaseActivity {
    private static Bitmap tabImageBitmap;

    @Bind(R.id.contentTab)
    TabLayout contentTabLayout;
    @Bind(R.id.contentViewPager)
    NoSwipePager pagerView;

    private List<TabLibrary> pageTabList = new ArrayList<>();
    private List<TabLibrary> tabLibraryList = new ArrayList<>();
    private List<GroupContainer> groupContainerList = new ArrayList<>();

    private boolean isColorDevice = false;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {
        isColorDevice = AppCompatUtils.isColorDevice(this);
    }

    @Override
    protected void initView() {
        initToolBar();
        initTableView();
        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EpdController.postInvalidate(getWindow().getDecorView().getRootView(), UpdateMode.GC);
    }

    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {
        final AuthTokenAction authTokenAction = new AuthTokenAction();
        final CloudGroupContainerListLoadAction groupLoadAction = new CloudGroupContainerListLoadAction();
        final ActionChain actionChain = new ActionChain();
        actionChain.addAction(authTokenAction);
        actionChain.addAction(groupLoadAction);
        actionChain.execute(InfoApp.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || CollectionUtils.isNullOrEmpty(groupLoadAction.getContainerList())) {
                    ToastUtils.showToast(getApplicationContext(), R.string.online_group_load_error);
                    return;
                }
                loadGroupLibraryList(groupContainerList = groupLoadAction.getContainerList());
                invalidateOptionsMenu();
            }
        });
    }

    private void loadGroupLibraryList(List<GroupContainer> groupContainerList) {
        List<Library> libraryList = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(groupContainerList)) {
            libraryList = groupContainerList.get(0).libraryList;
        }
        notifyDataChanged(libraryList);
    }

    private void notifyDataChanged(List<Library> list) {
        notifyTabLayoutChange(list);
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (Library library : list) {
            EventBus.getDefault().postSticky(new BookLibraryEvent(library));
        }
    }

    private List<TabLibrary> getExtraCustomTabLibrary() {
        List<TabLibrary> tabList = new ArrayList<>();
        TabLibrary tabLibrary = new TabLibrary(null);
        tabLibrary.action = TabAction.Account;
        tabLibrary.tabTitle = getString(R.string.main_item_user_info_title);
        tabList.add(tabLibrary);
        return tabList;
    }

    private void notifyTabLayoutChange(List<Library> list) {
        tabLibraryList.clear();
        if (!CollectionUtils.isNullOrEmpty(list)) {
            for (Library library : list) {
                tabLibraryList.add(new TabLibrary(library));
            }
        }
        tabLibraryList.addAll(getExtraCustomTabLibrary());
        addTabList(contentTabLayout, tabLibraryList);
    }

    private void initViewPager() {
        pagerView.addFilterScrollableViewClass(RecyclerView.class);
        pagerView.addFilterScrollableViewClass(ViewPager.class);
        pagerView.addOnPageChangeListener(new OnPageChangeListenerImpl());
        pagerView.setAdapter(new ViewPagerAdapter(pageTabList, getSupportFragmentManager()));
    }

    private void forceViewReLayout(View view) {
        if (isColorDevice) {
            AppCompatUtils.forceViewReLayout(view);
        }
    }

    private void selectTab(TabLayout tabLayout, int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            changeTheTabState(tabLayout.getTabAt(i), i == position);
        }
    }

    private void changeTheTabState(TabLayout.Tab tab, boolean select) {
        View itemView = tab.getCustomView();
        TextView textView = (TextView) itemView.findViewById(R.id.title_text);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.action_image);
        if (select) {
            textView.setTextColor(Color.WHITE);
            imageView.setVisibility(View.VISIBLE);
            forceViewReLayout(imageView);
        } else {
            textView.setTextColor(Color.BLACK);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    private int getDimensionPixelSize(int dimenResId) {
        return getResources().getDimensionPixelSize(dimenResId);
    }

    private Bitmap getTabImageBitmap() {
        if (!isColorDevice) {
            return null;
        }
        if (tabImageBitmap == null) {
            int width = getDimensionPixelSize(R.dimen.tab_layout_tab_max_width) - getDimensionPixelSize(R.dimen.tab_layout_tab_image_margin);
            Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.tab_background_selected);
            if (src == null) {
                return null;
            }
            Bitmap scale = Bitmap.createScaledBitmap(src, width / 2, src.getHeight() / 2, false);
            if (scale == null) {
                return null;
            }
            tabImageBitmap = QRCodeUtil.getCFABitMap(scale);
            src.recycle();
            scale.recycle();
        }
        return tabImageBitmap;
    }

    private void addTabList(TabLayout tabLayout, List<TabLibrary> tabLibraryList) {
        int position = 0;
        notifyTabLayoutChange(tabLayout, tabLibraryList);
        selectTab(tabLayout, position);
        switchToNewTab(position);
        notifyPageViewChange(tabLibraryList, position);
    }

    private void notifyTabLayoutChange(TabLayout tabLayout, List<TabLibrary> tabLibraryList) {
        tabLayout.removeAllTabs();
        if (CollectionUtils.isNullOrEmpty(tabLibraryList)) {
            return;
        }
        for (TabLibrary tabLibrary : tabLibraryList) {
            TabLayout.Tab tab = tabLayout.newTab().setCustomView(R.layout.tab_item_layout);
            final ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.action_image);
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.title_text);
            String title = tabLibrary.getTabTitle();
            textView.setText(title);
            Bitmap tabBitmap = getTabImageBitmap();
            if (tabBitmap != null) {
                imageView.setImageBitmap(tabBitmap);
            }
            tabLayout.addTab(tab);
        }
    }

    private void notifyPageViewChange(List<TabLibrary> tabLibraryList, int selectedPosition) {
        if (tabLibraryList == null) {
            tabLibraryList = new ArrayList<>();
        }
        pagerView.setCurrentItem(selectedPosition, false);
        pageTabList.clear();
        pageTabList.addAll(tabLibraryList);
        pagerView.setAdapter(new ViewPagerAdapter(pageTabList, getSupportFragmentManager()));
    }

    private void initToolBar() {
        initSupportActionBarWithCustomBackFunction();
    }

    private void initTableView() {
        disableA2ForSpecificView(contentTabLayout);
        contentTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        contentTabLayout.setSelectedTabIndicatorHeight(0);
        contentTabLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        contentTabLayout.addOnTabSelectedListener(new OnTabSelectedListenerImpl() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (pagerView.getAdapter().getCount() <= 0) {
                    return;
                }
                selectTab(contentTabLayout, tab.getPosition());
                pagerView.setCurrentItem(tab.getPosition(), false);
            }
        });
        addTabList(contentTabLayout, tabLibraryList);
        selectTab(contentTabLayout, 0);
    }

    private void disableA2ForSpecificView(View view) {
        Device.currentDevice().disableA2ForSpecificView(view);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (contentTabLayout.getSelectedTabPosition() == CollectionUtils.getSize(tabLibraryList) - 1) {
            EventBus.getDefault().post(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (filterKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }
        EventBus.getDefault().post(event);
        return true;
    }

    private boolean filterKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (CollectionUtils.isNullOrEmpty(groupContainerList)) {
            return super.onCreateOptionsMenu(menu);
        }
        for (int i = 0; i < groupContainerList.size(); i++) {
            menu.add(0, i, Menu.NONE, groupContainerList.get(i).group.name);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (CollectionUtils.isNullOrEmpty(groupContainerList)) {
            return super.onOptionsItemSelected(item);
        }
        notifyDataChanged(groupContainerList.get(item.getItemId()).libraryList);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataRefreshEvent(DataRefreshEvent event) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabSwitchEvent(TabSwitchEvent switchEvent) {
        processTabSwitchEvent(switchEvent);
    }

    private void processTabSwitchEvent(TabSwitchEvent switchEvent) {
        int position = contentTabLayout.getSelectedTabPosition();
        if (switchEvent.isNextTabSwitch()) {
            position++;
        } else {
            position--;
        }
        switchToNewTab(position);
    }

    private void switchToNewTab(int position) {
        TabLayout.Tab tab = contentTabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }
}
