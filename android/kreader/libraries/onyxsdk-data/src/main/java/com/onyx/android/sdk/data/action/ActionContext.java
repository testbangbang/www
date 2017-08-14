package com.onyx.android.sdk.data.action;

import android.content.Context;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;

/**
 * Created by suicheng on 2017/8/10.
 */
public class ActionContext {
    public Context context;
    public CloudManager cloudManager;
    public DataManager dataManager;

    public static ActionContext create(@NonNull Context context, @NonNull CloudManager cloudManager,
                                       @NonNull DataManager dataManager) {
        ActionContext actionContext = new ActionContext();
        actionContext.context = context;
        actionContext.cloudManager = cloudManager;
        actionContext.dataManager = dataManager;
        return actionContext;
    }
}
