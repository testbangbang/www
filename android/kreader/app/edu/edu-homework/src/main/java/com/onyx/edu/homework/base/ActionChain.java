package com.onyx.edu.homework.base;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class ActionChain {

    private List<BaseAction> actionList = new ArrayList<>();
    private boolean abortWhenException = false;

    public ActionChain() {
    }

    public ActionChain(boolean abortWhenException) {
        this.abortWhenException = abortWhenException;
    }

    public ActionChain addAction(final BaseAction action) {
        actionList.add(action);
        return this;
    }

    public void execute(final Context context, final BaseCallback callback) {
        final BaseAction action = actionList.remove(0);
        executeAction(context, action, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (isFinished(callback, request, e)) {
                    return;
                }
                execute(context, callback);
            }
        });
    }

    private void executeAction(final Context context, final BaseAction action, final BaseCallback callback) {
        action.execute(context, callback);
    }

    private boolean isFinished(final BaseCallback callback, final BaseRequest request, final Throwable e) {
        if (actionList.size() <= 0 || (abortWhenException && e != null)) {
            BaseCallback.invoke(callback, request, e);
            return true;
        }
        return false;
    }

}
