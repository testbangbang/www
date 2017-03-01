package com.onyx.kreader.ui;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.reader.common.Debug;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.utils.TreeObserverUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.requests.LoadDocumentOptionsRequest;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

public class ReaderTabHostActivity extends AppCompatActivity {

    private static final String TAG = ReaderTabHostActivity.class.getSimpleName();

    private static final String TAG_TAB_MANAGER = "tab_manager";

    private PowerManager.WakeLock startupWakeLock;

    private ReaderTabManager tabManager = ReaderTabManager.create();
    private TabHost tabHost;
    private String pathToContinueOpenAfterRotation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Debug.d(TAG, "onCreate");
        acquireStartupWakeLock();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_host);

        initComponents();
    }

    @Override
    protected void onResume() {
        Debug.d(TAG, "onResume, tab count: " + tabHost.getTabWidget().getTabCount());
        super.onResume();

        syncFullScreenState();

//        int orientation = SingletonSharedPreference.getScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        if (orientation != DeviceUtils.getScreenOrientation(this)) {
//            setRequestedOrientation(orientation);
//        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Debug.d(TAG, "onConfigurationChanged, tab count: " + tabHost.getTabWidget().getTabCount());
        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Debug.d(TAG, "onChangeOrientation -> onGlobalLayout");
                TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                updateReaderTabWindowHeight();
                if (StringUtils.isNotBlank(pathToContinueOpenAfterRotation)) {
                    openDocWithTab(pathToContinueOpenAfterRotation);
                    pathToContinueOpenAfterRotation = null;
                }
            }
        });
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        Debug.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Debug.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Debug.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Debug.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Debug.d(TAG, "onDestroy");
        super.onDestroy();
        releaseStartupWakeLock();
    }

    @Override
    public void onLowMemory() {
        Debug.e(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Debug.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        handleActivityIntent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Debug.d(TAG, "onSaveInstanceState: " + tabManager.toJson());
        outState.putString(TAG_TAB_MANAGER, tabManager.toJson());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Debug.d(TAG, "onRestoreInstanceState: " + savedInstanceState.getString(TAG_TAB_MANAGER));
        super.onRestoreInstanceState(savedInstanceState);
        tabManager = ReaderTabManager.createFromJson(savedInstanceState.getString(TAG_TAB_MANAGER));
        for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
            addTabToHost(entry.getKey(), entry.getValue());
        }
    }

    private void initComponents() {
        initTabHost();
        initReceiver();
    }

    private void initTabHost() {
        tabHost = (TabHost) findViewById(R.id.tab_host);
        tabHost.setup();

        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // delay the handling of activity intent from onCreate()
                TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                handleActivityIntent();
            }
        });

        if (!tabManager.supportMultipleTabs()) {
            return;
        }

        addTabToHost(ReaderTabManager.ReaderTab.TAB_1, "");
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                ReaderTabManager.ReaderTab tab = Enum.valueOf(ReaderTabManager.ReaderTab.class, tabId);
                if (tabManager.isTabOpened(tab)) {
                    reopenReaderTab(tab);
                }
            }
        });
        tabHost.setCurrentTab(0);
    }

    private void initReceiver() {
        ReaderTabHostBroadcastReceiver.setCallback(new ReaderTabHostBroadcastReceiver.Callback() {
            @Override
            public void onChangeOrientation(final int orientation) {
                Debug.d(TAG, "onChangeOrientation: " + orientation);
                setRequestedOrientation(orientation);
                SingletonSharedPreference.setScreenOrientation(orientation);
                tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        Debug.d(TAG, "onChangeOrientation -> onGlobalLayout");
                        TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                        updateReaderTabWindowHeight();
                    }
                });
            }

            @Override
            public void onEnterFullScreen() {
                syncFullScreenState();
            }

            @Override
            public void onQuitFullScreen() {
                syncFullScreenState();
            }
        });
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
                closeReaderTab(tab);
            }
        });
    }

    private void removeTabFromHost(ReaderTabManager.ReaderTab tab) {
        // clear first, or we'll get incorrect tab host state
        tabHost.clearAllTabs();
        for (LinkedHashMap.Entry<ReaderTabManager.ReaderTab, String> entry : tabManager.getOpenedTabs().entrySet()) {
            if (entry.getKey() == tab) {
                continue;
            }
            addTabToHost(entry.getKey(), entry.getValue());
        }
    }

    private void updateCurrentTabInHost(ReaderTabManager.ReaderTab tab) {
        if (tabHost.getTabWidget().getTabCount() > 0 &&
                !tabHost.getCurrentTabTag().equals(tab.toString())) {
            tabHost.setCurrentTabByTag(tab.toString());
        }
    }

    private ReaderTabManager.ReaderTab getCurrentTabInHost() {
        return Enum.valueOf(ReaderTabManager.ReaderTab.class, tabHost.getCurrentTabTag());
    }

    private int getTabContentHeight() {
        Debug.d(TAG, "tab host height: " + tabHost.getHeight() +
                ", tab widget height: " + tabHost.getTabWidget().getHeight() +
                ", tab content height: " + tabHost.getTabContentView().getHeight());
        return tabHost.getHeight() - tabHost.getTabWidget().getHeight();
    }

    private void acquireStartupWakeLock() {
        if (startupWakeLock == null) {
            startupWakeLock = Device.currentDevice().newWakeLock(this, ReaderActivity.class.getSimpleName());
        }
        if (startupWakeLock != null) {
            startupWakeLock.acquire();
        }
    }

    private void releaseStartupWakeLock() {
        if (startupWakeLock != null && startupWakeLock.isHeld()) {
            startupWakeLock.release();
            startupWakeLock = null;
        }
    }

    private void syncFullScreenState() {
        boolean fullScreen = !SingletonSharedPreference.isSystemStatusBarEnabled(this) || DeviceConfig.sharedInstance(this).isSupportColor();
        Debug.d(TAG, "syncFullScreenState: " + fullScreen);
        DeviceUtils.setFullScreenOnResume(this, fullScreen);
        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Debug.d(TAG, "syncFullScreenState -> onGlobalLayout");
                TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                updateReaderTabWindowHeight();
            }
        });
    }

    private boolean handleActivityIntent() {
        try {
            String action = getIntent().getAction();
            if (StringUtils.isNullOrEmpty(action)) {
            } else if (action.equals(Intent.ACTION_MAIN)) {
            } else if (action.equals(Intent.ACTION_VIEW)) {
                handleViewActionIntent();
                return true;
            }
            finish();
            return true;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            releaseStartupWakeLock();
        }
        return false;
    }

    private void handleViewActionIntent() {
        final String path = FileUtils.getRealFilePathFromUri(this, getIntent().getData());
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(path,
                null);
        DataManager dataProvider = new DataManager();
        dataProvider.submit(this, loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                if (!processOrientation(loadDocumentOptionsRequest.getDocumentOptions())) {
                    pathToContinueOpenAfterRotation = path;
                    return;
                }
                openDocWithTab(path);
            }
        });

    }

    private boolean processOrientation(final BaseOptions options) {
        int target = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (options != null && options.getOrientation() >= 0) {
            target = options.getOrientation();
        }
        int current = DeviceUtils.getScreenOrientation(this);
        Debug.d("current orientation: " + current + ", target orientation: " + target);
        if (current != target) {
            setRequestedOrientation(target);
            if (target == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                // reverse portrait will not trigger onConfigurationChanged() in activity,
                // so we process as orientation not changed
                return true;
            }
            return false;
        }
        return true;
    }

    private void openDocWithTab(String path) {
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
        addReaderTab(tab, path);

        Intent intent = new Intent(this, tabManager.getTabActivity(tab));
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), getIntent().getType());
        final int tabContentHeight = getTabContentHeight();
        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, tabContentHeight);
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
        if (tabManager.supportMultipleTabs()) {
            addTabToHost(tab, path);
            updateCurrentTabInHost(tab);
            updateReaderTabWindowHeight(tab);
        }
    }

    private void closeReaderTab(ReaderTabManager.ReaderTab tab) {
        closeTabActivity(tab);
        tabManager.removeOpenedTab(tab);
        removeTabFromHost(tab);

        if (tabManager.getOpenedTabs().size() <= 0) {
            finish();
        }
    }

    private void closeTabActivity(ReaderTabManager.ReaderTab tab) {
        Intent intent = new Intent(this, tabManager.getTabReceiver(tab));
        intent.setAction(ReaderBroadcastReceiver.ACTION_CLOSE);
        Debug.d(TAG, "sendBroadcast: " + intent);
        sendBroadcast(intent);
    }

    private void reopenReaderTab(ReaderTabManager.ReaderTab tab) {
        if (!bringReaderTabToFront(tab)) {
            openDocWithTab(tab, tabManager.getOpenedTabs().get(tab));
        }
    }

    private boolean bringReaderTabToFront(ReaderTabManager.ReaderTab tab) {
        String clzName = tabManager.getTabActivity(tab).getName();
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if(!tasksList.isEmpty()){
            int nSize = tasksList.size();
            for(int i = 0; i < nSize;  i++){
                if(tasksList.get(i).topActivity.getClassName().equals(clzName)){
                    updateCurrentTabInHost(tab);
                    updateReaderTabWindowHeight(tab);
                    am.moveTaskToFront(tasksList.get(i).id, 0);
                    return true;
                }
            }
        }
        return false;
    }

    private void updateReaderTabWindowHeight() {
        updateReaderTabWindowHeight(getCurrentTabInHost());
    }

    private void updateReaderTabWindowHeight(ReaderTabManager.ReaderTab tab) {
        final int tabContentHeight = getTabContentHeight();
        Intent intent = new Intent(this, tabManager.getTabReceiver(tab));
        intent.setAction(ReaderBroadcastReceiver.ACTION_RESIZE_WINDOW);
        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, tabContentHeight);
        sendBroadcast(intent);
    }

}
