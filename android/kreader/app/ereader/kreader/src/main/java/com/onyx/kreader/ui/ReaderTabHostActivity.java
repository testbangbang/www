package com.onyx.kreader.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.LoadDocumentOptionsRequest;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TreeObserverUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReaderTabHostActivity extends OnyxBaseActivity {

    private static final Class TAG = ReaderTabHostActivity.class;

    public static AtomicBoolean tabWidgetVisible = new AtomicBoolean(true);
    public static AtomicBoolean enableDebugLog = null;

    private WakeLockHolder startupWakeLock = new WakeLockHolder();

    private ReaderTabManager tabManager = ReaderTabManager.create();
    private TabHost tabHost;
    private TabWidget tabWidget;
    private RelativeLayout layoutMenu;
    private ImageView btnMenu;
    private ImageView btnSwitch;
    private View divider;
    private String pathToContinueOpenAfterRotation;

    private boolean isFront = true;

    private boolean insideTabChanging = false;
    private boolean isManualShowTab = true;

    private boolean isSideReading = false;
    // 0: left, 1: right
    private ReaderTabManager.ReaderTab[] sideReadingTabs = new ReaderTabManager.ReaderTab[2];

    private boolean isDoubleOpen = false;
    private boolean isDoubleLinked = false;

    private ReaderTabManager.ReaderTab tabBeforeSideReading;
    private int orientationBeforeSideReading;

    private DeviceReceiver deviceReceiver = new DeviceReceiver();

    private ReaderTabHostBroadcastReceiver.Callback callback = new ReaderTabHostBroadcastReceiver.Callback() {
        @Override
        public void onTabBringToFront(String tabActivity) {
            Debug.d(getClass(), "onTabBringToFront: " + tabActivity);
            ensureFront();

            for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
                if (tabActivity.compareTo(tabManager.getTabActivity(entry.getKey()).getCanonicalName()) == 0) {
                    if (ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, entry.getKey(), tabWidgetVisible.get())) {
                        updateCurrentTabInHost(entry.getKey());
                    }
                    return;
                }
            }
        }

        @Override
        public void onTabBackPressed() {
            onBackPressed();
        }

        @Override
        public void onChangeOrientation(final int orientation) {
            final int current = DeviceUtils.getScreenOrientation(ReaderTabHostActivity.this);
            Debug.d("onChangeOrientation, current: " + current + ", target: " + orientation);
            setRequestedOrientation(orientation);
            SingletonSharedPreference.setScreenOrientation(orientation);
            if (current != orientation && DeviceUtils.isReverseOrientation(current, orientation)) {
                onScreenOrientationChanged();
            }
        }

        @Override
        public void onEnterFullScreen() {
            SingletonSharedPreference.setBooleanValue(getString(R.string.settings_enable_system_status_bar_key), false);
            syncFullScreenState();
        }

        @Override
        public void onQuitFullScreen() {
            SingletonSharedPreference.setBooleanValue(getString(R.string.settings_enable_system_status_bar_key), true);
            syncFullScreenState();
        }

        @Override
        public void onUpdateTabWidgetVisibility(boolean visible) {
            updateTabWidgetVisibility(visible);
        }

        @Override
        public void onOpenDocumentFailed(String path) {
            if (isSideReading) {
                quitSideReadingMode();
                return;
            }
            closeTabIfOpenFileFailed(path);
        }

        @Override
        public void onSideReading(ReaderTabHostBroadcastReceiver.SideReadingCallback callback, String leftDocPath, String rightDocPath) {
            onSideReadingCallback(callback, leftDocPath, rightDocPath);
        }

        @Override
        public void onGotoPageLink(String link) {
            gotoPageLink(link);
        }

        @Override
        public void onEnableDebugLog() {
            setEnableDebugLog(true);
            ReaderTabActivityManager.enableDebugLog(ReaderTabHostActivity.this, tabManager, true);
        }

        @Override
        public void onDisableDebugLog() {
            setEnableDebugLog(false);
            ReaderTabActivityManager.enableDebugLog(ReaderTabHostActivity.this, tabManager, false);
        }
    };

    public static void setTabWidgetVisible(boolean visible) {
        ReaderTabHostActivity.tabWidgetVisible.set(visible);
    }

    public static void setEnableDebugLog(boolean enabled) {
        Debug.setDebug(enabled);
        enableDebugLog = new AtomicBoolean(enabled);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        acquireStartupWakeLock();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reader_host);
        initComponents();
        restoreReaderTabState();

        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Debug.d(TAG, "onCreate -> tab host onLayoutChange");
                updateReaderTabWindowHeight();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncFullScreenState();
        syncTabState();
    }

    @Override
    protected void onResume() {
        isFront = true;
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                onScreenOrientationChanged();
            }
        });
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        saveReaderTabState();
        super.onPause();
    }

    @Override
    protected void onStop() {
        isFront = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ReaderTabHostBroadcastReceiver.setCallback(null);
        deviceReceiver.enable(this, false);
        saveReaderTabState();
        super.onDestroy();
        releaseStartupWakeLock();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleActivityIntent();
    }

    @Override
    public void onBackPressed() {
        Debug.d(getClass(), "onBackPressed");
        if (isSideReading) {
            quitSideReadingMode();
        }

        // move background reader tabs to back first, so we can avoid unintended screen update
        ReaderTabManager.ReaderTab currentTab = getCurrentTabInHost();

        // force close all tabs in case the bug of tab opened but not in opened tab list
        ReaderTabManager.ReaderTab[] allTabs = ReaderTabManager.ReaderTab.values();
        for (ReaderTabManager.ReaderTab tab : allTabs) {
            if (tab != currentTab) {
                ReaderTabActivityManager.moveReaderTabToBack(this, tabManager, tab);
            }
        }

        ReaderTabActivityManager.moveReaderTabToBack(this, tabManager, currentTab);
        finish();
    }

    private void initComponents() {
        initTabHost();
        initReceiver();
    }

    private void initTabHost() {
        tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        layoutMenu = (RelativeLayout) findViewById(R.id.layout_menu);
        btnMenu = (ImageView) findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReaderTabActivityManager.showTabHostMenuDialog(ReaderTabHostActivity.this,
                        tabManager, getCurrentTabInHost(), btnMenu);
            }
        });
        btnSwitch = (ImageView) findViewById(R.id.btn_switch);
        divider = findViewById(R.id.divider);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                updateTabLayoutState(!isManualShowTab);
                updateTabWidgetVisibility(false);
            }
        });

        tabHost = (TabHost) findViewById(R.id.tab_host);
        tabHost.setup();

        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // delay the handling of activity intent from onCreate()
                Debug.d(TAG, "initTabHost -> onGlobalLayout");
                TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                handleActivityIntent();
            }
        });

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                final ReaderTabManager.ReaderTab tab = Enum.valueOf(ReaderTabManager.ReaderTab.class, tabId);
                if (!insideTabChanging && tabManager.isTabOpened(tab)) {
                    if (closeTabIfFileNotExists(tab)) {
                        return;
                    }

                    reopenReaderTab(tab);
                }
                ReaderTabActivityManager.notifyTabActivated(ReaderTabHostActivity.this, tabManager, tab);
            }
        });

        addDummyTabToHost();

        if (tabManager.supportMultipleTabs()) {
            hideTabWidget();
        }
    }

    private boolean isSideReadingLeft(ReaderTabManager.ReaderTab tab) {
        return sideReadingTabs[0] == tab;
    }

    private ReaderTabManager.ReaderTab getSideReadingLeft() {
        return sideReadingTabs[0];
    }

    private void setSideReadingLeft(ReaderTabManager.ReaderTab tab) {
        sideReadingTabs[0] = tab;
    }

    private boolean isSideReadingRight(ReaderTabManager.ReaderTab tab) {
        return sideReadingTabs[1] == tab;
    }

    private ReaderTabManager.ReaderTab getSideReadingRight() {
        return sideReadingTabs[1];
    }

    private void setSideReadingRight(ReaderTabManager.ReaderTab tab) {
        sideReadingTabs[1] = tab;
    }

    private void startSideReadingMode(String left, String right, boolean sideNoteMode) {
        saveReaderTabState();

        tabBeforeSideReading = getCurrentTabInHost();
        ReaderTabActivityManager.notifyStopNoteWriting(this, tabManager, tabBeforeSideReading);

        isSideReading = true;

        tabManager.resetTabState();

        ReaderTabManager.ReaderTab leftTab = getFreeReaderTab();
        // it's a trick here,
        // by not using tabBeforeSideReading, when we quit side reading mode,
        // we can switch back to the tab without waiting
        while (leftTab == tabBeforeSideReading) {
            leftTab = getFreeReaderTab();
        }
        ReaderTabManager.ReaderTab rightTab = getFreeReaderTab();
        while (rightTab == tabBeforeSideReading) {
            rightTab = getFreeReaderTab();
        }
        setSideReadingLeft(leftTab);
        setSideReadingRight(rightTab);

        addOpenedTab(leftTab, left);
        addOpenedTab(rightTab, right);
        rebuildTabWidget();

        ReaderTabActivityManager.bringTabToFront(this, tabManager, leftTab, tabWidgetVisible.get());
        openDocWithTab(leftTab, left);

        ReaderTabActivityManager.bringTabToFront(this, tabManager, rightTab, tabWidgetVisible.get());
        openDocWithTab(rightTab, right);

        findViewById(R.id.dash_line_splitter).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_menu).setVisibility(View.GONE);
        findViewById(R.id.btn_switch).setVisibility(View.GONE);

        orientationBeforeSideReading = DeviceUtils.getScreenOrientation(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    private void quitSideReadingMode() {
        isSideReading = false;

        if (isDoubleOpen) {
            isDoubleOpen = false;
            isDoubleLinked = false;
        }

        findViewById(R.id.dash_line_splitter).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_menu).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_switch).setVisibility(View.VISIBLE);

        closeReaderTab(getSideReadingLeft(), true, false);
        closeReaderTab(getSideReadingRight(), true, false);

        setSideReadingLeft(null);
        setSideReadingRight(null);

        restoreReaderTabState();

        openDocWithTab(tabBeforeSideReading, tabManager.getOpenedTabs().get(tabBeforeSideReading));
        tabBeforeSideReading = null;

        setRequestedOrientation(orientationBeforeSideReading);
    }

    private void switchSideReadingTab() {
        ReaderTabManager.ReaderTab tab = getSideReadingLeft();
        setSideReadingLeft(getSideReadingRight());
        setSideReadingRight(tab);

        rebuildTabWidget();

        updateReaderTabWindowHeight(getSideReadingLeft());
        updateReaderTabWindowHeight(getSideReadingRight());
    }

    private boolean closeReaderIfFileNotExists() {
        final OnyxCustomDialog dlg = OnyxCustomDialog.getConfirmDialog(ReaderTabHostActivity.this,
                getResources().getString(R.string.file_not_exists),
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }, null);
        showDialog(dlg);
        return true;
    }

    private boolean closeTabIfFileNotExists(final ReaderTabManager.ReaderTab tab) {
        File file = new File(tabManager.getOpenedTabs().get(tab));
        if (file.exists()) {
            return false;
        }
        final OnyxCustomDialog dlg = OnyxCustomDialog.getConfirmDialog(ReaderTabHostActivity.this,
                getResources().getString(R.string.file_not_exists),
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeReaderTab(tab);
                    }
                }, null);
        bringSelfToFront();
        dlg.show();
        return true;
    }

    private boolean closeTabIfOpenFileFailed(final String path) {
        final ReaderTabManager.ReaderTab tab = tabManager.findOpenedTabByPath(path);
        if (tab == null) {
            return false;
        }
        // no need to close reader activity again, as it's already closed
        closeReaderTab(tab, false, true);
        return true;
    }

    private void gotoPageLink(final String link) {
        ReaderTabManager.ReaderTab tab = getCurrentTabInHost();

        ReaderTabManager.ReaderTab dst = tab;
        if (isDoubleLinked) {
            dst = isSideReadingLeft(tab) ? getSideReadingRight() : getSideReadingLeft();
        }
        ReaderTabActivityManager.notifyGotoPageLink(this, tabManager, dst, link);
    }

    private void updateTabLayoutState(boolean show) {
        isManualShowTab = show;
        tabWidget.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        btnSwitch.setImageResource(show ? R.drawable.ic_unfold : R.drawable.ic_pack_up);
    }

    private void initReceiver() {
        ReaderTabHostBroadcastReceiver.setCallback(callback);

        deviceReceiver.initReceiver(this);
        deviceReceiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {

            @Override
            public void onMediaUnmounted(Intent intent) {
                Debug.d(TAG, "onMediaUnmounted: " + intent);
                if (EnvironmentUtil.isExternalStorageDirectory(ReaderTabHostActivity.this, intent)) {
                    // when device is connected to PC as UMS, close reader for safe
                    deviceReceiver.enable(ReaderTabHostActivity.this, false);
                    onBackPressed();
                } else {
                    ReaderTabHostActivity.this.onMediaUnmounted(intent);
                }
            }

            @Override
            public void onMediaBadRemoval(Intent intent) {
                Debug.d(TAG, "onMediaBadRemoval: " + intent);
                ReaderTabHostActivity.this.onMediaUnmounted(intent);
            }
        });
        deviceReceiver.setMtpEventListener(new DeviceReceiver.MtpEventListener() {
            @Override
            public void onMtpEvent(Intent intent) {
                ReaderTabHostActivity.this.onMediaUnmounted(intent);
            }
        });
    }

    private void onMediaUnmounted(Intent intent) {
        final String media = FileUtils.getRealFilePathFromUri(this, intent.getData());
        ReaderTabManager.ReaderTab tab = getCurrentTabInHost();
        if (!tabManager.isTabOpened(tab)) {
            return;
        }
        String path = tabManager.getOpenedTabs().get(tab);
        if (!path.startsWith(media)) {
            return;
        }
        closeReaderIfFileNotExists();
    }

    private void onScreenOrientationChanged() {
        Debug.d(TAG, "onScreenOrientationChanged");
        int current = DeviceUtils.getScreenOrientation(this);
        if (isSideReading && (current == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
                current == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)) {
            quitSideReadingMode();
            return;
        }

        showTabWidgetOnCondition();
        updateReaderTabWindowHeight();

        // in some cases, tab host activity will be blocked by tab activity, so force it to be front
        bringSelfToFront();
        if (StringUtils.isNotBlank(pathToContinueOpenAfterRotation)) {
            openDocFromIntent(pathToContinueOpenAfterRotation);
            pathToContinueOpenAfterRotation = null;
        } else if (tabManager.getOpenedTabs().size() > 0) {
            ReaderTabManager.ReaderTab currentTab = getCurrentTabInHost();
            if (isSideReading) {
                ReaderTabActivityManager.bringTabToFront(this, tabManager, getSideReadingLeft(), tabWidgetVisible.get());
                ReaderTabActivityManager.bringTabToFront(this, tabManager, getSideReadingRight(), tabWidgetVisible.get());
            }

            ReaderTabActivityManager.bringTabToFront(this, tabManager, currentTab, tabWidgetVisible.get());
        }
    }

    private void addTabToHost(final ReaderTabManager.ReaderTab tab, final String path) {
        final String name = FileUtils.getFileName(path);

        final TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getTabCount(); i++) {
            if (tabWidget.getChildTabViewAt(i).getTag() == tab) {
                ((TextView)tabWidget.getChildAt(i).findViewById(R.id.text_view_title)).setText(name);
                if (isSideReading) {
                    tabWidget.getChildAt(i).findViewById(R.id.image_button_menu).setVisibility(View.VISIBLE);
                } else {
                    tabWidget.getChildAt(i).findViewById(R.id.image_button_menu).setVisibility(View.GONE);
                }
                return;
            }
        }

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.view_reader_host_tab_indicator, null);
        ((TextView)tabIndicator.findViewById(R.id.text_view_title)).setText(name);
        if (isSideReading) {
            tabIndicator.findViewById(R.id.image_button_menu).setVisibility(View.VISIBLE);
        } else {
            tabIndicator.findViewById(R.id.image_button_menu).setVisibility(View.GONE);
        }

        tabHost.addTab(tabHost.newTabSpec(tab.toString())
                .setIndicator(tabIndicator)
                .setContent(android.R.id.tabcontent));
        tabWidget.getChildTabViewAt(tabWidget.getTabCount() - 1).setTag(tab);

        final View button = tabIndicator.findViewById(R.id.image_button_menu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, tab, tabWidgetVisible.get());
                ReaderTabActivityManager.showTabHostMenuDialog(ReaderTabHostActivity.this, tabManager, tab, button);
            }
        });

        tabIndicator.findViewById(R.id.image_button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSideReading) {
                    quitSideReadingMode();
                }
                closeReaderTab(tab);
            }
        });
    }

    private void addDummyTabToHost() {
        addTabToHost(ReaderTabManager.ReaderTab.TAB_1, "");
        tabHost.setCurrentTab(0);
    }

    private void rebuildTabWidget() {
        Debug.d(TAG, "rebuilding tab widget:");
        insideTabChanging = true;
        try {
            ReaderTabManager.ReaderTab currentTab = getCurrentTabInHost();
            tabHost.clearAllTabs();

            if (isSideReading) {
                addTabToHost(getSideReadingLeft(), tabManager.getOpenedTabs().get(getSideReadingLeft()));
                addTabToHost(getSideReadingRight(), tabManager.getOpenedTabs().get(getSideReadingRight()));
            } else {
                ArrayList<LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String>> reverseList = new ArrayList<>();
                for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
                    reverseList.add(0, entry);
                }

                for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : reverseList) {
                    addTabToHost(entry.getKey(), entry.getValue());
                }

                if (tabWidget.getTabCount() <= 0) {
                    addDummyTabToHost();
                }
            }

            if (currentTab != null) {
                tabHost.setCurrentTabByTag(currentTab.toString());
            }
        } finally {
            insideTabChanging = false;
        }
    }

    private void updateCurrentTabInHost(ReaderTabManager.ReaderTab tab) {
        if (tabHost.getTabWidget().getTabCount() > 0 &&
                !tabHost.getCurrentTabTag().equals(tab.toString())) {
            insideTabChanging = true;
            tabHost.setCurrentTabByTag(tab.toString());
            insideTabChanging = false;
        }
    }

    private ReaderTabManager.ReaderTab getCurrentTabInHost() {
        return Enum.valueOf(ReaderTabManager.ReaderTab.class, tabHost.getCurrentTabTag());
    }

    private void updateTabTitle(ReaderTabManager.ReaderTab tab, String title) {
        final TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getTabCount(); i++) {
            if (tabWidget.getChildTabViewAt(i).getTag() == tab) {
                ((TextView)tabWidget.getChildAt(i).findViewById(R.id.text_view_title)).setText(title);
                break;
            }
        }
    }

    private boolean isShowingTabWidget() {
        return ReaderTabManager.supportMultipleTabs() && tabWidgetVisible.get();
    }

    private void showTabWidgetOnCondition() {
        if (isShowingTabWidget()) {
            showTabWidget();
            updateTabLayoutState(isManualShowTab);
        } else {
            hideTabWidget();
        }
    }

    private void showTabWidget() {
        tabHost.getTabWidget().setVisibility(View.VISIBLE);
        layoutMenu.setVisibility(View.VISIBLE);
    }

    private void hideTabWidget() {
        tabHost.getTabWidget().setVisibility(View.INVISIBLE);
        layoutMenu.setVisibility(View.INVISIBLE);
    }

    private int getTabContentWidth() {
        return tabHost.getWidth();
    }

    private int getTabContentHeight() {
        Debug.d(TAG, "tab host height: " + tabHost.getHeight() +
                ", tab widget height: " + tabHost.getTabWidget().getHeight() +
                ", tab content height: " + tabHost.getTabContentView().getHeight());
        if (!isShowingTabWidget()) {
            return tabHost.getHeight();
        }
        return tabHost.getHeight() - tabHost.getTabWidget().getHeight() - divider.getHeight();
    }

    private int getTabWindowGravity(ReaderTabManager.ReaderTab tab) {
        if (!isSideReading) {
            return Gravity.BOTTOM;
        }

        if (isSideReadingLeft(tab)) {
            return Gravity.BOTTOM | Gravity.LEFT;
        } else {
            return Gravity.BOTTOM | Gravity.RIGHT;
        }
    }

    private int getTabWindowWidth(ReaderTabManager.ReaderTab tab) {
        if (!isSideReading) {
            return WindowManager.LayoutParams.MATCH_PARENT;
        }

        int splitLineWidth = findViewById(R.id.dash_line_splitter).getWidth();
        return (getTabContentWidth() - splitLineWidth) / 2;
    }

    private int getTabWindowHeight(ReaderTabManager.ReaderTab tab) {
        return getTabContentHeight();
    }

    private void acquireStartupWakeLock() {
        if (startupWakeLock == null) {
            startupWakeLock = new WakeLockHolder();
        }

        startupWakeLock.acquireWakeLock(this, ReaderActivity.class.getSimpleName());
    }

    private void releaseStartupWakeLock() {
        startupWakeLock.releaseWakeLock();
    }

    private void syncFullScreenState() {
        boolean fullScreen = !SingletonSharedPreference.isSystemStatusBarEnabled(this) || DeviceConfig.sharedInstance(this).isSupportColor();
        Debug.d(TAG, "syncFullScreenState: " + DeviceUtils.isFullScreen(tabHost) + " -> " + fullScreen);
        if (fullScreen != DeviceUtils.isFullScreen(tabHost)) {
            tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Debug.d(TAG, "syncFullScreenState -> onGlobalLayout");
                    TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                    updateReaderTabWindowHeight();
                    bringSelfToFront();
                    if (isSideReading) {
                        ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, getSideReadingLeft(), tabWidgetVisible.get());
                        ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, getSideReadingRight(), tabWidgetVisible.get());
                    }
                    ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, getCurrentTabInHost(), tabWidgetVisible.get());
                }
            });
        }
        DeviceUtils.setFullScreenOnResume(this, fullScreen);
    }

    private void syncTabState() {
        ReaderTabManager.ReaderTab currentTab = getCurrentTabInHost();
        if (!tabManager.supportMultipleTabs()) {
            for (ReaderTabManager.ReaderTab tab : tabManager.getOpenedTabs().keySet()) {
                if (tab == currentTab) {
                    continue;
                }
                ReaderTabActivityManager.closeTabActivity(this, tabManager, tab);
            }
        }

        tabManager.resetTabState(getCurrentTabInHost());
        rebuildTabWidget();

        if (!tabManager.supportMultipleTabs()) {
            tabWidgetVisible.set(true);
            ReaderTabActivityManager.updateTabWidgetVisibilityOnOpenedReaderTabs(this, tabManager, true);
        }
    }

    private boolean handleActivityIntent() {
        try {
            String action = getIntent().getAction();
            if (StringUtils.isNullOrEmpty(action)) {
            } else if (action.equals(Intent.ACTION_MAIN)) {
            } else if (action.equals(Intent.ACTION_VIEW)) {
                handleViewActionIntent();
                return true;
            } else if (action.equals(ReaderTabHostBroadcastReceiver.ACTION_TAB_BACK_PRESSED)) {
                callback.onTabBackPressed();
                return true;
            } else if (action.equals(ReaderTabHostBroadcastReceiver.ACTION_TAB_BRING_TO_FRONT)) {
                callback.onTabBringToFront(getIntent().getStringExtra(ReaderTabHostBroadcastReceiver.TAG_TAB_ACTIVITY));
                return true;
            }
            return true;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            releaseStartupWakeLock();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleViewActionIntent();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handleViewActionIntent() {
        acquireStartupWakeLock();

        if (isSideReading) {
            quitSideReadingMode();
        }

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            return;
        }
        final String path = FileUtils.getRealFilePathFromUri(this, getIntent().getData());
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(path,
                null);
        DataManager dataProvider = new DataManager();
        dataProvider.submit(this, loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                try {
                    if (e != null) {
                        return;
                    }
                    BaseOptions baseOptions = loadDocumentOptionsRequest.getDocumentOptions();
                    DeviceConfig.adjustOptionsWithDeviceConfig(baseOptions, ReaderTabHostActivity.this);
                    if (waitScreenOrientationChanging(baseOptions)) {
                        pathToContinueOpenAfterRotation = path;
                        return;
                    }
                    openDocFromIntent(path);
                } finally {
                    releaseStartupWakeLock();
                }
            }
        });

    }

    private boolean waitScreenOrientationChanging(final BaseOptions options) {
        int target = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (options != null && options.getOrientation() >= 0) {
            target = options.getOrientation();
        }
        int current = DeviceUtils.getScreenOrientation(this);
        Debug.d("current orientation: " + current + ", target orientation: " + target);
        if (current != target) {
            //noinspection ResourceType
            setRequestedOrientation(target);
            if (DeviceUtils.isReverseOrientation(current, target)) {
                // reverse orientation will not trigger onConfigurationChanged() in activity,
                // so we process as orientation not changed
                return false;
            }
            return true;
        }
        return false;
    }

    private void openDocFromIntent(String path) {
        // ensure tab host is visible before opening the doc
        bringSelfToFront();

        ReaderTabManager.ReaderTab tab = tabManager.findOpenedTabByPath(path);
        if (tab != null) {
            Debug.d(TAG, "file already opened in tab: " + tab + ", " + path);
            reopenReaderTab(tab);
            return;
        }

        tab = getFreeReaderTab();
        openDocWithTab(tab, path, getIntent());
        addOpenedTab(tab, path);
        updateReaderTab(tab);
    }

    private void openDocWithTab(ReaderTabManager.ReaderTab tab, String path) {
        openDocWithTab(tab, path, null);
    }

    private void openDocWithTab(ReaderTabManager.ReaderTab tab, String path, Intent srcIntent) {
        final int gravity = getTabWindowGravity(tab);
        final int width = getTabWindowWidth(tab);
        final int height = getTabWindowHeight(tab);

        ReaderTabActivityManager.openDocument(this, tabManager, tab, srcIntent, path,
                gravity, width, height, tabWidgetVisible.get(), isSideReading);
    }

    private ReaderTabManager.ReaderTab getFreeReaderTab() {
        ReaderTabManager.ReaderTab tab = tabManager.pollFreeTab();
        if (tab != null) {
            return tab;
        }
        tab = tabManager.reuseOpenedTab();
        return tab;
    }

    private void addOpenedTab(ReaderTabManager.ReaderTab tab, String path) {
        tabManager.addOpenedTab(tab, path);
    }

    private void updateReaderTab(ReaderTabManager.ReaderTab tab) {
        showTabWidgetOnCondition();
        rebuildTabWidget();
        updateCurrentTabInHost(tab);
        updateReaderTabWindowHeight(tab);
    }

    private void closeReaderTab(ReaderTabManager.ReaderTab tab) {
        closeReaderTab(tab, true, true);
    }

    private void closeReaderTab(ReaderTabManager.ReaderTab tab, boolean closeTabActivity, boolean cleanUpTabWidget) {
        if (closeTabActivity) {
            ReaderTabActivityManager.closeTabActivity(this, tabManager, tab);
        }
        tabManager.removeOpenedTab(tab);

        if (!cleanUpTabWidget) {
            return;
        }

        showTabWidgetOnCondition();
        rebuildTabWidget();

        if (tabManager.getOpenedTabs().size() <= 0) {
            finish();
        } else {
            reopenReaderTab(getCurrentTabInHost());
        }
    }

    private void reopenReaderTab(ReaderTabManager.ReaderTab tab) {
        if (!ReaderTabActivityManager.bringTabToFront(this, tabManager, tab, tabWidgetVisible.get())) {
            openDocWithTab(tab, tabManager.getOpenedTabs().get(tab));
        }
        updateCurrentTabInHost(tab);
        updateReaderTabWindowHeight(tab);
    }

    private void ensureFront() {
        if (!isFront) {
            bringSelfToFront();
        }
    }

    private boolean bringSelfToFront() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                if (tasksList.get(i).topActivity.getClassName().equals(getClass().getName())) {
                    Debug.d(TAG, "bringSelfToFront: success!");
                    am.moveTaskToFront(tasksList.get(i).id, 0);
                    return true;
                }
            }
        }
        Debug.d(TAG, "bringSelfToFront: failed!");
        return false;
    }

    private void updateReaderTabWindowHeight() {
        if (isSideReading) {
            updateReaderTabWindowHeight(getSideReadingLeft());
            updateReaderTabWindowHeight(getSideReadingRight());
        } else {
            if (tabHost.getTabWidget().getTabCount() > 0) {
                updateReaderTabWindowHeight(getCurrentTabInHost());
            }
        }
    }

    private void updateReaderTabWindowHeight(ReaderTabManager.ReaderTab tab) {
        final int gravity = getTabWindowGravity(tab);
        final int width = getTabWindowWidth(tab);
        final int height = getTabWindowHeight(tab);

        ReaderTabActivityManager.updateTabWindow(this, tabManager, tab,
                gravity, width, height);
    }

    private void saveReaderTabState() {
        Debug.d(TAG, "saveReaderTabState");
        if (isSideReading) {
            return;
        }
        SingletonSharedPreference.setMultipleTabState(tabManager.toJson());
        SingletonSharedPreference.setMultipleTabVisibility(tabWidgetVisible.get());
    }

    private void restoreReaderTabState() {
        Debug.d(TAG, "restoreReaderTabState");
        tabManager = ReaderTabManager.createFromJson(SingletonSharedPreference.getMultipleTabState());
        tabWidgetVisible.set(SingletonSharedPreference.getMultipleTabVisibility());
        showTabWidgetOnCondition();
        rebuildTabWidget();
    }

    private void updateTabWidgetVisibility(boolean visible) {
        setTabWidgetVisible(visible);
        showTabWidgetOnCondition();
        updateReaderTabWindowHeight();
        ReaderTabActivityManager.updateTabWidgetVisibilityOnOpenedReaderTabs(this, tabManager, visible);

        saveReaderTabState();
    }

    private void showDialog(Dialog dlg) {
        tabHost.setBackgroundColor(Color.TRANSPARENT);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sendBroadcast(new Intent(com.onyx.kreader.note.receiver.DeviceReceiver.SYSTEM_UI_DIALOG_CLOSE_ACTION));
                tabHost.setBackgroundColor(Color.WHITE);
                if (isSideReading) {
                    ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, getSideReadingLeft(), tabWidgetVisible.get());
                    ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, getSideReadingRight(), tabWidgetVisible.get());
                } else if (getCurrentTabInHost() != null) {
                    ReaderTabActivityManager.bringTabToFront(ReaderTabHostActivity.this, tabManager, getCurrentTabInHost(), tabWidgetVisible.get());
                }
            }
        });
        bringSelfToFront();
        dlg.show();
        sendBroadcast(new Intent(com.onyx.kreader.note.receiver.DeviceReceiver.SYSTEM_UI_DIALOG_OPEN_ACTION));
    }

    private void onSideReadingCallback(ReaderTabHostBroadcastReceiver.SideReadingCallback callback,
                                       String leftDocPath, String rightDocPath) {
        switch (callback) {
            case DOUBLE_OPEN:
                startSideReadingMode(leftDocPath, leftDocPath, false);
                isDoubleOpen = true;
                isDoubleLinked = true;
                break;
            case SIDE_OPEN:
                startSideReadingMode(leftDocPath, rightDocPath, false);
                break;
            case OPEN_NEW_DOC:
                ReaderTabManager.ReaderTab tab = tabManager.findOpenedTabByPath(leftDocPath);
                if (tab != null) {
                    reopenReaderTab(tab);
                    return;
                }
                openDocWithTab(getCurrentTabInHost(), leftDocPath);
                updateTabTitle(getCurrentTabInHost(), FileUtils.getFileName(leftDocPath));
                break;
            case SWITCH_SIDE:
                switchSideReadingTab();
                break;
            case QUIT_SIDE_READING:
                if (isSideReading) {
                    quitSideReadingMode();
                }
                break;
        }
    }

}
