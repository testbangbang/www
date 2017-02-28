package com.onyx.kreader.ui;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.reader.utils.TreeObserverUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ReaderTabHostActivity extends AppCompatActivity {

    private static final String TAG = ReaderTabHostActivity.class.getSimpleName();

    private static final String TAG_OPENED_TABS = "opened_tabs";

    private enum ReaderTab {
        TAB_1, TAB_2, TAB_3, TAB_4
    }

    private static HashMap<ReaderTab, Class<?>> tabActivityList = new HashMap<>();
    private static HashMap<ReaderTab, Class<?>> tabReceiverList = new HashMap<>();
    private static Queue<ReaderTab> freeTabList = new LinkedList<>();
    private static LinkedHashMap<ReaderTab, String> openedTabs = new LinkedHashMap<>();

    static {
        tabActivityList.put(ReaderTab.TAB_1, Reader_Tab_1_Activity.class);
        tabActivityList.put(ReaderTab.TAB_2, Reader_Tab_2_Activity.class);
        tabActivityList.put(ReaderTab.TAB_3, Reader_Tab_3_Activity.class);
        tabActivityList.put(ReaderTab.TAB_4, Reader_Tab_4_Activity.class);

        tabReceiverList.put(ReaderTab.TAB_1, Reader_Tab_1_BroadcastReceiver.class);
        tabReceiverList.put(ReaderTab.TAB_2, Reader_Tab_2_BroadcastReceiver.class);
        tabReceiverList.put(ReaderTab.TAB_3, Reader_Tab_3_BroadcastReceiver.class);
        tabReceiverList.put(ReaderTab.TAB_4, Reader_Tab_4_BroadcastReceiver.class);

        freeTabList.add(ReaderTab.TAB_1);
        freeTabList.add(ReaderTab.TAB_2);
        freeTabList.add(ReaderTab.TAB_3);
        freeTabList.add(ReaderTab.TAB_4);
    }

    private PowerManager.WakeLock startupWakeLock;

    private TabHost tabHost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        acquireStartupWakeLock();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_host);

        initTabHost();

        ReaderTabHostBroadcastReceiver.setCallback(new ReaderTabHostBroadcastReceiver.Callback() {
            @Override
            public void onChangeOrientation(final int orientation) {
                Log.d(TAG, "onChangeOrientation: " + orientation);
                setRequestedOrientation(orientation);
                SingletonSharedPreference.setScreenOrientation(orientation);
                tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        Log.d(TAG, "onChangeOrientation -> onGlobalLayout");
                        TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                        updateWindowHeight();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        syncFullScreenState();

        int orientation = SingletonSharedPreference.getScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (orientation != DeviceUtils.getScreenOrientation(this)) {
            setRequestedOrientation(orientation);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        releaseStartupWakeLock();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        handleActivityIntent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: " + JSON.toJSONString(openedTabs));
        outState.putString(TAG_OPENED_TABS, JSON.toJSONString(openedTabs));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: " + savedInstanceState.getString(TAG_OPENED_TABS));
        super.onRestoreInstanceState(savedInstanceState);
        LinkedHashMap<String, String> map = JSON.parseObject(savedInstanceState.getString(TAG_OPENED_TABS), openedTabs.getClass());
        for (LinkedHashMap.Entry<String, String> entry : map.entrySet()) {
            openedTabs.put(Enum.valueOf(ReaderTab.class, entry.getKey()), entry.getValue());
        }
        for (LinkedHashMap.Entry<ReaderTab, String> entry : openedTabs.entrySet()) {
            freeTabList.remove(entry.getKey());
            addTabToHost(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void onLowMemory() {
        Log.e(TAG, "onLowMemory");
        super.onLowMemory();
    }

    private void initTabHost() {
        tabHost = (TabHost) findViewById(R.id.tab_host);
        tabHost.setup();

//        addTabToHost(ReaderTab.TAB_1, "TAB");

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                ReaderTab tab = Enum.valueOf(ReaderTab.class, tabId);
                if (isTabOpened(tab)) {
                    reopenTab(tab);
                }
            }
        });

        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                handleActivityIntent();
            }
        });

        tabHost.setCurrentTab(0);
    }

    private void addTabToHost(final ReaderTab tab, final String path) {
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

    private void removeTabFromHost(ReaderTab tab) {
        tabHost.clearAllTabs();

        for (LinkedHashMap.Entry<ReaderTab, String> entry : openedTabs.entrySet()) {
            if (entry.getKey() == tab) {
                continue;
            }
            addTabToHost(entry.getKey(), entry.getValue());
        }

        return;
    }

    private void updateCurrentTab(ReaderTab tab) {
        if (tabHost.getTabWidget().getTabCount() > 1 &&
                !tabHost.getCurrentTabTag().equals(tab.toString())) {
            tabHost.setCurrentTabByTag(tab.toString());
        }
    }

    private void syncFullScreenState() {
        boolean fullScreen = !SingletonSharedPreference.isSystemStatusBarEnabled(this) || DeviceConfig.sharedInstance(this).isSupportColor();
        Log.d(TAG, "syncFullScreenState: " + fullScreen);
        DeviceUtils.setFullScreenOnResume(this, fullScreen);
        tabHost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "syncFullScreenState -> onGlobalLayout");
                TreeObserverUtils.removeGlobalOnLayoutListener(tabHost.getViewTreeObserver(), this);
                updateWindowHeight();
            }
        });
    }

    private int getTabContentHeight() {
        Log.d(TAG, "tab host height: " + tabHost.getHeight() +
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

        ReaderTab tab = findOpenedTabByPath(path);
        if (tab != null) {
            Log.d(TAG, "file already opened in tab: " + tab + ", " + getIntent().getDataString());
            if (isTabOpened(tab)) {
                reopenTab(tab);
                return;
            }
        }

        tab = getFreeTab();
        Intent intent = new Intent(this, getTabActivity(tab));
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(getIntent().getData(), getIntent().getType());
        final int tabContentHeight = getTabContentHeight();
        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, tabContentHeight);
        startActivity(intent);

        addReaderTab(tab, path);
    }

    private void addReaderTab(ReaderTab tab, String path) {
        addOpenedTab(tab, path);
        if (openedTabs.size() < 2) {
            return;
        } else if (openedTabs.size() == 2) {
             ReaderTab firstTab = openedTabs.keySet().iterator().next();
            addTabToHost(firstTab, openedTabs.get(firstTab));
            updateWindowHeight();
        }
        addTabToHost(tab, path);
        updateCurrentTab(tab);
    }

    private void closeReaderTab(ReaderTab tab) {
        closeTabActivity(tab);
        removeOpenedTab(tab);

        if (openedTabs.size() > 1) {
            removeTabFromHost(tab);
        } else {
            tabHost.clearAllTabs();
            updateWindowHeight();
        }
    }

    private void closeTabActivity(ReaderTab tab) {
        Intent intent = new Intent(this, getTabReceiver(tab));
        intent.setAction(ReaderBroadcastReceiver.ACTION_CLOSE);
        Log.d(TAG, "sendBroadcast: " + intent);
        sendBroadcast(intent);
    }

    private ReaderTab getFreeTab() {
        if (!freeTabList.isEmpty()) {
            return freeTabList.poll();
        }
        return openedTabs.keySet().iterator().next();
    }

    private void addOpenedTab(ReaderTab tab, String path) {
        openedTabs.put(tab, path);
    }

    private void removeOpenedTab(ReaderTab tab) {
        freeTabList.add(tab);
        openedTabs.remove(tab);
    }

    private ReaderTab findOpenedTabByPath(String path) {
        for (Map.Entry<ReaderTab, String> entry : openedTabs.entrySet()) {
            if (entry.getValue().compareTo(path) == 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Class getTabActivity(ReaderTab tab) {
        return tabActivityList.get(tab);
    }

    private Class getTabReceiver(ReaderTab tab) {
        return tabReceiverList.get(tab);
    }

    private boolean isTabOpened(ReaderTab tab) {
        return openedTabs.containsKey(tab);
    }

    private void reopenTab(ReaderTab tab) {
        String clzName = getTabActivity(tab).getName();
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksList = am.getRunningTasks(Integer.MAX_VALUE);
        if(!tasksList.isEmpty()){
            int nSize = tasksList.size();
            for(int i = 0; i < nSize;  i++){
                if(tasksList.get(i).topActivity.getClassName().equals(clzName)){
                    updateWindowHeight();
                    am.moveTaskToFront(tasksList.get(i).id, 0);
                    updateCurrentTab(tab);
                    return;
                }
            }
        }

        Intent intent = new Intent(this, getTabActivity(tab));
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(openedTabs.get(tab))), getIntent().getType());
        final int tabContentHeight = getTabContentHeight();
        intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, tabContentHeight);
        startActivity(intent);
    }

    private void updateWindowHeight() {
        final int tabContentHeight = getTabContentHeight();
        for (ReaderTab tab : openedTabs.keySet()) {
            Intent intent = new Intent(this, getTabReceiver(tab));
            intent.setAction(ReaderBroadcastReceiver.ACTION_RESIZE_WINDOW);
            intent.putExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, tabContentHeight);
            sendBroadcast(intent);
        }
    }

}
