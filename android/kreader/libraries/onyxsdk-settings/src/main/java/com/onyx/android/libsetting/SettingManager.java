package com.onyx.android.libsetting;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by solskjaer49 on 2017/2/11 18:44.
 */

public class SettingManager {

    private DataManager dataManager;
    private CloudStore cloudStore;
    private static SettingManager instance;

    private SettingManager() {
        dataManager = new DataManager();
        cloudStore = new CloudStore();
    }

    static public SettingManager sharedInstance() {
        if (instance == null) {
            instance = new SettingManager();
        }
        return instance;
    }

    public boolean submitCloudRequest(final Context context, final BaseCloudRequest request, final BaseCallback callback) {
        return cloudStore.submitRequest(context, request, callback);
    }

    public boolean submitDataRequest(final Context context, final BaseDataRequest request, final BaseCallback callback) {
        dataManager.submit(context, request, callback);
        return true;
    }
}
