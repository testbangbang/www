package com.onyx.edu.student.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/11/18.
 */
public class AppStatusReceiver extends BroadcastReceiver {

    static public class AppStateListener {
        public void onAppStateChanged(Intent intent) {
        }
    }

    private AppStateListener appStateListener;

    public void setAppStateListener(AppStateListener listener) {
        this.appStateListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (appStateListener != null) {
            appStateListener.onAppStateChanged(intent);
        }
    }

    public void initReceiver(Context context, List<String> actionList) {
        enable(context, actionList);
    }

    private void enable(Context context, List<String> actionList) {
        try {
            if (CollectionUtils.isNullOrEmpty(actionList)) {
                actionList = getDefaultActionList();
            }
            context.registerReceiver(this, appStatusFilter(actionList));
        } catch (Exception e) {
        }
    }

    public void disable(Context context) {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
        }
    }

    private IntentFilter appStatusFilter(List<String> actionList) {
        IntentFilter appStatusFilter = new IntentFilter();
        appStatusFilter.addDataScheme("package");
        for (String action : actionList) {
            appStatusFilter.addAction(action);
        }
        return appStatusFilter;
    }

    private List<String> getDefaultActionList() {
        List<String> list = new ArrayList<>();
        list.add(Intent.ACTION_PACKAGE_ADDED);
        list.add(Intent.ACTION_PACKAGE_REPLACED);
        list.add(Intent.ACTION_PACKAGE_REMOVED);
        if (Build.VERSION.SDK_INT >= 14) {
            list.add(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        }
        return list;
    }
}
