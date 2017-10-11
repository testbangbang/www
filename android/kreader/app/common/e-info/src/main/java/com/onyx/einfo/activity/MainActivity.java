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

import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.GroupContainer;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.einfo.R;
import com.onyx.einfo.InfoApp;
import com.onyx.einfo.action.ActionChain;
import com.onyx.einfo.action.AuthTokenAction;
import com.onyx.einfo.action.CloudGroupContainerListLoadAction;
import com.onyx.einfo.adapter.ViewPagerAdapter;
import com.onyx.einfo.custom.NoSwipePager;
import com.onyx.einfo.device.DeviceConfig;
import com.onyx.einfo.events.BookLibraryEvent;
import com.onyx.einfo.events.DataRefreshEvent;
import com.onyx.einfo.events.SortByEvent;
import com.onyx.einfo.events.TabSwitchEvent;
import com.onyx.einfo.events.ViewTypeEvent;
import com.onyx.einfo.listener.OnPageChangeListenerImpl;
import com.onyx.einfo.listener.OnTabSelectedListenerImpl;
import com.onyx.einfo.manager.ConfigPreferenceManager;
import com.onyx.einfo.model.MenuAction;
import com.onyx.einfo.model.MenuCustomItem;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private DialogProgressHolder progressHolder = new DialogProgressHolder();
    private List<TabLibrary> pageTabList = new ArrayList<>();
    private List<TabLibrary> tabLibraryList = new ArrayList<>();
    private List<GroupContainer> groupContainerList = new ArrayList<>();

    private boolean isColorDevice = false;
    private CloudGroup currentGroup;

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
        Device.currentDevice().showSystemStatusBar(getApplicationContext());
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
                progressHolder.dismissProgressDialog(actionChain);
                if (e != null || CollectionUtils.isNullOrEmpty(groupLoadAction.getContainerList())) {
                    ToastUtils.showToast(getApplicationContext(), R.string.online_group_load_error);
                    return;
                }
                loadGroupLibraryList(groupContainerList = groupLoadAction.getContainerList());
                invalidateOptionsMenu();
            }
        });
        progressHolder.showProgressDialog(this, actionChain, R.string.refreshing, null);
    }

    private void loadGroupLibraryList(List<GroupContainer> groupContainerList) {
        int index = ConfigPreferenceManager.getCloudGroupSelected(getApplicationContext());
        if (index >= CollectionUtils.getSize(groupContainerList)) {
            index = 0;
        }
        processGroupSelect(index);
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

    private TabLibrary getAccountTabLibrary() {
        TabLibrary tabLibrary = new TabLibrary(null);
        tabLibrary.action = TabAction.Account;
        tabLibrary.tabTitle = getString(R.string.main_item_user_info_title);
        return tabLibrary;
    }

    private TabLibrary getMessageTabLibrary() {
        TabLibrary tabLibrary = new TabLibrary(null);
        tabLibrary.action = TabAction.Message;
        tabLibrary.tabTitle = getString(R.string.main_item_message);
        return tabLibrary;
    }

    private List<TabLibrary> getExtraCustomTabLibrary() {
        List<TabLibrary> tabList = new ArrayList<>();
        tabList.add(getMessageTabLibrary());
        tabList.add(getAccountTabLibrary());
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
            int width = getDimensionPixelSize(R.dimen.tab_layout_tab_max_width) -
                    getDimensionPixelSize(R.dimen.tab_layout_tab_image_margin);
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
        if (tabLayout == null) {
            return;
        }
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
        progressHolder.dismissAllProgressDialog();
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
        List<MenuCustomItem> list = MenuCustomItem.getMenuItemList(DeviceConfig.sharedInstance(this)
                .getContentMenuItemList());
        if (CollectionUtils.isNullOrEmpty(list)) {
            return super.onCreateOptionsMenu(menu);
        }
        for (int i = 0; i < list.size(); i++) {
            menu.add(0, list.get(i).titleRes, Menu.NONE, list.get(i).titleRes);
        }
        if (!hasMoreGroup()) {
            for (MenuCustomItem item : list) {
                if (item.action == MenuAction.Group) {
                    menu.removeItem(item.titleRes);
                }
            }
        }
        return true;
    }

    private boolean hasMoreGroup() {
        return CollectionUtils.getSize(groupContainerList) > 1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<MenuCustomItem> list = MenuCustomItem.getMenuItemList(DeviceConfig.sharedInstance(this)
                .getContentMenuItemList());
        MenuCustomItem selectItem = null;
        for (MenuCustomItem menuCustomItem : list) {
            if (menuCustomItem.titleRes == item.getItemId()) {
                selectItem = menuCustomItem;
                break;
            }
        }
        processItemSelected(selectItem);
        return true;
    }

    private void processItemSelected(MenuCustomItem item) {
        if (item == null) {
            return;
        }
        switch (item.action) {
            case ViewType:
                processViewTypeSwitch();
                break;
            case SortBy:
                showSortByDialog();
                break;
            case Group:
                showGroupSelectDialog();
                break;
        }
    }


    private void processViewTypeSwitch() {
        ViewType viewType = ConfigPreferenceManager.getViewType(this);
        viewType = (viewType == ViewType.Thumbnail) ? ViewType.Details : ViewType.Thumbnail;
        EventBus.getDefault().post(ViewTypeEvent.create(viewType));
        ConfigPreferenceManager.setViewType(this, viewType);
    }

    private void showSortByDialog() {
        final Map<String, SortBy> sortByMap = new LinkedHashMap<>();
        sortByMap.put(getString(R.string.by_name), SortBy.Ordinal);
        sortByMap.put(getString(R.string.by_creation_time), SortBy.CreationTime);
        List<String> contentList = Arrays.asList(sortByMap.keySet().toArray(new String[0]));
        DialogSortBy dialog = getSortByDialog(contentList, new DialogSortBy.OnSortByListener() {
            @Override
            public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                processSortBySwitch(sortByMap.get(sortBy), sortOrder);
            }
        });
        dialog.setCurrentSortBySelectedIndex(getCurrentSortByIndex(ConfigPreferenceManager.getCloudSortBy(this), sortByMap));
        dialog.setCurrentSortOrderSelected(ConfigPreferenceManager.getCloudSortOrder(this));
        dialog.show(getFragmentManager());
    }

    private int getCurrentSortByIndex(SortBy currentSortBy, Map<String, SortBy> sortByMap) {
        if (currentSortBy == null) {
            return 0;
        }
        int index = Arrays.asList(sortByMap.values().toArray(new SortBy[0])).indexOf(currentSortBy);
        if (index < 0) {
            return 0;
        }
        return index;
    }

    private void processSortBySwitch(SortBy sortBy, SortOrder sortOrder) {
        ConfigPreferenceManager.setCloudSortBy(this, sortBy);
        ConfigPreferenceManager.setCloudSortOrder(this, sortOrder);
        EventBus.getDefault().post(SortByEvent.create(sortBy, sortOrder));
    }

    private void showGroupSelectDialog() {
        if (!hasMoreGroup()) {
            return;
        }
        final Map<String, CloudGroup> groupMap = new LinkedHashMap<>();
        for (GroupContainer groupContainer : groupContainerList) {
            groupMap.put(groupContainer.group.name, groupContainer.group);
        }
        List<String> contentList = Arrays.asList(groupMap.keySet().toArray(new String[0]));
        DialogSortBy dialog = getSortByDialog(contentList, new DialogSortBy.OnSortByListener() {
            @Override
            public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                ConfigPreferenceManager.setCloudGroupSelected(getApplicationContext(), position);
                processGroupSelect(position);
            }
        });
        dialog.setShowSortOrderLayout(false);
        dialog.setCurrentSortBySelectedIndex(getCurrentGroupIndex());
        dialog.show(getFragmentManager());
    }

    private DialogSortBy getSortByDialog(List<String> contentList, DialogSortBy.OnSortByListener listener) {
        DialogSortBy dialog = new DialogSortBy(null, contentList);
        dialog.setOnSortByListener(listener);
        dialog.setAlignParams(getDialogAlignParams());
        return dialog;
    }

    private DialogSortBy.AlignLayoutParams getDialogAlignParams(){
        DialogSortBy.AlignLayoutParams alignParams = new DialogSortBy.AlignLayoutParams(
                getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_x_pos),
                getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_y_pos));
        alignParams.width = 240;
        alignParams.height = 220;
        return alignParams;
    }

    private int getCurrentGroupIndex() {
        for (int i = 0; i < groupContainerList.size(); i++) {
            CloudGroup group = groupContainerList.get(i).group;
            if (group._id.equals(currentGroup._id)) {
                return i;
            }
        }
        return 0;
    }

    private void processGroupSelect(int index) {
        List<Library> libraryList = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(groupContainerList)) {
            currentGroup = groupContainerList.get(index).group;
            libraryList = groupContainerList.get(index).libraryList;
        }
        notifyDataChanged(libraryList);
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
