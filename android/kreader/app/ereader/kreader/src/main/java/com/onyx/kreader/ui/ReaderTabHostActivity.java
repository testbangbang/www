package com.onyx.kreader.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.reader.host.request.LoadDocumentOptionsRequest;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.TreeObserverUtils;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
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
    private LinearLayout layoutMenu;
    private ImageView btnMenu;
    private ImageView btnSwitch;
    private String pathToContinueOpenAfterRotation;

    private boolean insideTabChanging = false;
    private boolean isManualShowTab = true;

    private boolean isSideReading = false;
    // 0: left, 1: right
    private ReaderTabManager.ReaderTab[] sideReadingTabs = new ReaderTabManager.ReaderTab[2];

    private DeviceReceiver deviceReceiver = new DeviceReceiver();

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
        for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
            if (entry.getKey() != currentTab) {
                moveReaderTabToBack(entry.getKey());
            }
        }
        moveReaderTabToBack(currentTab);
        finish();
    }

    private void initComponents() {
        initTabHost();
        initReceiver();
    }

    private void initTabHost() {
        tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        layoutMenu = (LinearLayout) findViewById(R.id.layout_menu);
        btnMenu = (ImageView) findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabManager.getOpenedTabs().size() < 2) {
                    return;
                }
                if (!isSideReading) {
                    startSideReadingMode();
                } else {
                    quitSideReadingMode();
                }
            }
        });
        btnSwitch = (ImageView) findViewById(R.id.btn_switch);
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
                onTabSwitched(tab);
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

    private void startSideReadingMode() {
        if (tabManager.getOpenedTabs().size() < 2) {
            return;
        }

        isSideReading = true;
        int i = 0;
        for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
            if (i == 1) {
                setSideReadingLeft(entry.getKey());
            } else if (i == 0) {
                setSideReadingRight(entry.getKey());
            }
            i++;
        }

        rebuildTabWidget();

        updateReaderTabWindowHeight(getSideReadingLeft());
        updateReaderTabWindowHeight(getSideReadingRight());
        bringReaderTabToFront(getSideReadingLeft());
        bringReaderTabToFront(getSideReadingRight());

    }

    private void quitSideReadingMode() {
        isSideReading = false;
        rebuildTabWidget();

        updateReaderTabWindowHeight(getSideReadingLeft());
        updateReaderTabWindowHeight(getSideReadingRight());
        setSideReadingLeft(null);
        setSideReadingRight(null);

        bringReaderTabToFront(getSideReadingLeft());
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
        bringSelfToFront();
        dlg.show();
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

    private ReaderTabManager.ReaderTab findOpenedTabByPath(final String path) {
        for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
            if (entry.getValue().compareTo(path) == 0) {
                return entry.getKey();

            }
        }
        return null;
    }

    private boolean closeTabIfOpenFileFailed(final String path) {
        final ReaderTabManager.ReaderTab tab = findOpenedTabByPath(path);
        if (tab == null) {
            return false;
        }
        // no need to close reader activity again, as it's already closed
        closeReaderTab(tab, false);
        return true;
    }

    private void updateTabLayoutState(boolean show) {
        isManualShowTab = show;
        tabWidget.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        btnSwitch.setImageResource(show ? R.drawable.ic_unfold : R.drawable.ic_pack_up);
    }

    private void initReceiver() {
        ReaderTabHostBroadcastReceiver.setCallback(new ReaderTabHostBroadcastReceiver.Callback() {
            @Override
            public void onTabBringToFront(String path) {
                ReaderTabManager.ReaderTab tab = tabManager.findOpenedTabByPath(path);
                if (tab != null) {
                    bringReaderTabToFront(tab);
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
                if (current != orientation && isReverseOrientation(current, orientation)) {
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
                closeTabIfOpenFileFailed(path);
            }

            @Override
            public void onEnableDebugLog() {
                enableDebugLog(true);
            }

            @Override
            public void onDisableDebugLog() {
                enableDebugLog(false);
            }
        });

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

    private boolean isReverseOrientation(int current, int target) {
        return (current == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && target == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) ||
                (current == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT && target == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ||
                (current == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && target == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) ||
                (current == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE && target == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void onScreenOrientationChanged() {
        Debug.d(TAG, "onScreenOrientationChanged");
        if (isSideReading) {
            quitSideReadingMode();
        }

        showTabWidgetOnCondition();
        updateReaderTabWindowHeight();

        // in some cases, tab host activity will be blocked by tab activity, so force it to be front
        bringSelfToFront();
        if (StringUtils.isNotBlank(pathToContinueOpenAfterRotation)) {
            openDocWithTab(pathToContinueOpenAfterRotation);
            pathToContinueOpenAfterRotation = null;
        } else if (tabManager.getOpenedTabs().size() > 0) {
            bringReaderTabToFront(getCurrentTabInHost());
        }
    }

    private void addTabToHost(final ReaderTabManager.ReaderTab tab, final String path) {
        final String name = FileUtils.getFileName(path);

        final TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getTabCount(); i++) {
            if (tabWidget.getChildTabViewAt(i).getTag() == tab) {
                ((TextView)tabWidget.getChildAt(i).findViewById(R.id.text_view_title)).setText(name);
                return;
            }
        }

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.view_reader_host_tab_indicator, null);
        ((TextView)tabIndicator.findViewById(R.id.text_view_title)).setText(name);

        tabHost.addTab(tabHost.newTabSpec(tab.toString())
                .setIndicator(tabIndicator)
                .setContent(android.R.id.tabcontent));
        tabWidget.getChildTabViewAt(tabWidget.getTabCount() - 1).setTag(tab);
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

                Debug.d(TAG, "rebuilding tab widget:");
                for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : reverseList) {
                    Debug.d(TAG, "rebuilding: " + entry.getKey());
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

    private boolean isShowingTabWidget() {
        return tabWidgetVisible.get() && tabManager.getOpenedTabs().size() > 1;
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
        return tabHost.getHeight() - tabHost.getTabWidget().getHeight();
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
                    bringReaderTabToFront(getCurrentTabInHost());
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
                closeTabActivity(tab);
            }
        }

        tabManager.resetTabState(getCurrentTabInHost());
        rebuildTabWidget();

        if (!tabManager.supportMultipleTabs()) {
            tabWidgetVisible.set(true);
            updateTabWidgetVisibilityOnOpenedReaderTabs(true);
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
                onBackPressed();
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
                    openDocWithTab(path);
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
            setRequestedOrientation(target);
            if (isReverseOrientation(current, target)) {
                // reverse orientation will not trigger onConfigurationChanged() in activity,
                // so we process as orientation not changed
                return false;
            }
            return true;
        }
        return false;
    }

    private void openDocWithTab(String path) {
        // ensure tab host is visible before opening the doc
        bringSelfToFront();

        ReaderTabManager.ReaderTab tab = tabManager.findOpenedTabByPath(path);
        if (tab != null) {
            Debug.d(TAG, "file already opened in tab: " + tab + ", " + path);
            reopenReaderTab(tab);
            return;
        }

        tab = getFreeReaderTab();
        openDocWithTab(tab, path);
    }

    private void openDocWithTab(ReaderTabManager.ReaderTab tab, String path) {
        Debug.d(TAG, "openDocWithTab: " + tab + ", " + path);
        addReaderTab(tab, path);

        Intent intent = new Intent(this, tabManager.getTabActivity(tab));
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), getIntent().getType());
        intent.putExtras(getIntent());
        final int tabContentHeight = getTabContentHeight();
        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, tabContentHeight);
        if (enableDebugLog != null) {
            intent.putExtra(ReaderBroadcastReceiver.TAG_ENABLE_DEBUG, enableDebugLog.get());
        }
        if (tabManager.getOpenedTabs().size() > 1) {
            intent.putExtra(ReaderBroadcastReceiver.TAG_TAB_WIDGET_VISIBLE, tabWidgetVisible.get());
        }

        startActivity(intent);
    }

    private ReaderTabManager.ReaderTab getFreeReaderTab() {
        ReaderTabManager.ReaderTab tab = tabManager.pollFreeTab();
        if (tab != null) {
            return tab;
        }
        tab = tabManager.reuseOpenedTab();
//        closeTabActivity(tab);
        return tab;
    }

    private void addReaderTab(ReaderTabManager.ReaderTab tab, String path) {
        tabManager.addOpenedTab(tab, path);

        showTabWidgetOnCondition();
        rebuildTabWidget();
        updateCurrentTabInHost(tab);
        updateReaderTabWindowHeight(tab);
    }

    private void closeReaderTab(ReaderTabManager.ReaderTab tab) {
        closeReaderTab(tab, true);
    }

    private void closeReaderTab(ReaderTabManager.ReaderTab tab, boolean closeTabActivity) {
        if (closeTabActivity) {
            closeTabActivity(tab);
        }
        tabManager.removeOpenedTab(tab);

        showTabWidgetOnCondition();
        rebuildTabWidget();

        if (tabManager.getOpenedTabs().size() <= 0) {
            finish();
        } else {
            reopenReaderTab(getCurrentTabInHost());
        }
    }

    private void closeTabActivity(ReaderTabManager.ReaderTab tab) {
        ReaderBroadcastReceiver.sendCloseReaderIntent(this, tabManager.getTabReceiver(tab));
    }

    private void reopenReaderTab(ReaderTabManager.ReaderTab tab) {
        if (!bringReaderTabToFront(tab)) {
            openDocWithTab(tab, tabManager.getOpenedTabs().get(tab));
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

    private boolean bringReaderTabToFront(ReaderTabManager.ReaderTab tab) {
        if (!tabManager.getOpenedTabs().containsKey(tab)) {
            return false;
        }

        String clzName = tabManager.getTabActivity(tab).getName();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                    Debug.d(TAG, "bring tab to front succeeded: " + tab);
                    updateCurrentTabInHost(tab);
                    updateReaderTabWindowHeight(tab);
                    am.moveTaskToFront(tasksList.get(i).id, 0);

                    if (!tabManager.supportMultipleTabs() || tabManager.getOpenedTabs().size() <= 1) {
                        ReaderBroadcastReceiver.sendUpdateTabWidgetVisibilityIntent(this,
                                tabManager.getTabReceiver(tab), true);
                    } else {
                        ReaderBroadcastReceiver.sendUpdateTabWidgetVisibilityIntent(this,
                                tabManager.getTabReceiver(tab), tabWidgetVisible.get());
                    }
                    return true;
                }
            }
        }
        Debug.d(TAG, "bring tab to front failed: " + tab);
        return false;
    }

    private boolean moveReaderTabToBack(ReaderTabManager.ReaderTab tab) {
        if (!tabManager.getOpenedTabs().containsKey(tab)) {
            return false;
        }

        String clzName = tabManager.getTabActivity(tab).getName();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                    Debug.d(TAG, "move tab to back succeeded: " + tab);
                    ReaderBroadcastReceiver.sendMoveTaskToBackIntent(this, tabManager.getTabReceiver(tab));
                    return true;
                }
            }
        }
        Debug.d(TAG, "move tab to back failed: " + tab);
        return false;
    }

    private void updateReaderTabWindowHeight() {
        if (tabHost.getTabWidget().getTabCount() > 0) {
            updateReaderTabWindowHeight(getCurrentTabInHost());
        }
    }

    private void updateReaderTabWindowHeight(ReaderTabManager.ReaderTab tab) {
        final int tabContentHeight = getTabContentHeight();
        Debug.d(TAG, "updateReaderTabWindowHeight: " + tab + ", " + tabContentHeight);
        if (!tabManager.getOpenedTabs().containsKey(tab)) {
            return;
        }

        if (!isSideReading) {
            ReaderBroadcastReceiver.sendResizeReaderWindowIntent(this,
                    tabManager.getTabReceiver(tab),
                    Gravity.BOTTOM,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    tabContentHeight);
            return;
        }

        if (isSideReadingLeft(tab)) {
            ReaderBroadcastReceiver.sendResizeReaderWindowIntent(this,
                    tabManager.getTabReceiver(tab),
                    Gravity.BOTTOM | Gravity.LEFT,
                    getTabContentWidth() / 2,
                    tabContentHeight);
        } else {
            ReaderBroadcastReceiver.sendResizeReaderWindowIntent(this,
                    tabManager.getTabReceiver(tab),
                    Gravity.BOTTOM | Gravity.RIGHT,
                    getTabContentWidth() / 2,
                    tabContentHeight);
        }
    }

    private void onTabSwitched(final ReaderTabManager.ReaderTab tab) {
        final String path = tabManager.getOpenedTabs().get(tab);
        for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
            ReaderBroadcastReceiver.sendDocumentActivatedIntent(this,
                    tabManager.getTabReceiver(entry.getKey()), path);
        }
    }

    private void saveReaderTabState() {
        Debug.d(TAG, "saveReaderTabState");
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

    private void enableDebugLog(boolean enabled) {
        Log.d(TAG.getSimpleName(), "enableDebugLog: " + enabled);
        setEnableDebugLog(enabled);
        enableDebugLogOnOpenedReaderTabs(enabled);
    }

    private void enableDebugLogOnOpenedReaderTabs(boolean enabled) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                for (ReaderTabManager.ReaderTab tab : tabManager.getOpenedTabs().keySet()) {
                    String clzName = tabManager.getTabActivity(tab).getName();
                    if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                        Debug.d(TAG, "set debug log: " + tab + ", " + enabled);
                        if (enabled) {
                            ReaderBroadcastReceiver.sendEnableDebugLogIntent(this, tabManager.getTabReceiver(tab));
                        } else {
                            ReaderBroadcastReceiver.sendDisableDebugLogIntent(this, tabManager.getTabReceiver(tab));
                        }
                        break;
                    }
                }
            }
        }
    }

    private void updateTabWidgetVisibility(boolean visible) {
        setTabWidgetVisible(visible);
        showTabWidgetOnCondition();
        updateReaderTabWindowHeight();
        updateTabWidgetVisibilityOnOpenedReaderTabs(visible);

        saveReaderTabState();
    }

    private void updateTabWidgetVisibilityOnOpenedReaderTabs(boolean visible) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if (!tasksList.isEmpty()) {
            int nSize = tasksList.size();
            for (int i = 0; i < nSize; i++) {
                for (ReaderTabManager.ReaderTab tab : tabManager.getOpenedTabs().keySet()) {
                    String clzName = tabManager.getTabActivity(tab).getName();
                    if (tasksList.get(i).topActivity.getClassName().equals(clzName)) {
                        Debug.d(TAG, "update tab widget visibility: " + tab + ", " + visible);
                        ReaderBroadcastReceiver.sendUpdateTabWidgetVisibilityIntent(this, tabManager.getTabReceiver(tab), visible);
                        break;
                    }
                }
            }
        }
    }
}
