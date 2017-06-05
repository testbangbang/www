package com.onyx.android.sdk.data.manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/5/23.
 */

public class PushActionManager {

    public static List<String> PUSH_DATA = new ArrayList<>();
    public static List<String> PUSH_CHANNEL = new ArrayList<>();


    private DataManager dataManager = new DataManager();
    private CloudManager cloudManager = new CloudManager();

    private Map<String, PushAction> pushActionMap = new HashMap<>();

    public abstract static class PushAction {
        public abstract void execute(PushActionContext actionContext);
    }

    public void addAction(String action, PushAction pushAction) {
        if (StringUtils.isNullOrEmpty(action) || pushAction == null) {
            return;
        }
        pushActionMap.put(action, pushAction);
    }

    public void processPushAction(Context context, Intent intent) {
        String action = intent.getAction();
        if (StringUtils.isNotBlank(action) && pushActionMap.containsKey(action)) {
            pushActionMap.get(action).execute(PushActionContext.create(context, getCloudManager(), getDataManager(), intent));
        }
    }

    public static String getPushData(Intent intent) {
        for (int i = 0; i < PushActionManager.PUSH_DATA.size(); i++) {
            String data = intent.getExtras().getString(PushActionManager.PUSH_DATA.get(i));
            if (StringUtils.isNotBlank(data)) {
                return data;
            }
        }
        return null;
    }

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public void setCloudManager(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public static class PushActionContext {
        public Context context;
        public CloudManager cloudManager;
        public DataManager dataManager;
        public Intent intent;

        private PushActionContext() {
        }

        public static PushActionContext create(@NonNull Context context, @NonNull CloudManager cloudManager, @NonNull DataManager dataManager,
                                               @NonNull Intent intent) {
            PushActionContext actionContext = new PushActionContext();
            actionContext.context = context;
            actionContext.cloudManager = cloudManager;
            actionContext.dataManager = dataManager;
            actionContext.intent = intent;
            return actionContext;
        }
    }
}
