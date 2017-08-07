package com.onyx.einfo.action;

import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/5/18.
 */

public class ActionChain {

    private List<BaseAction> actionList = new ArrayList<>();

    public ActionChain addAction(final BaseAction action) {
        actionList.add(action);
        return this;
    }

    public void execute(final LibraryDataHolder dataHolder, final BaseCallback callback) {
        if (isFinished(callback)) {
            return;
        }

        final BaseAction action = actionList.remove(0);
        executeAction(dataHolder, action, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                execute(dataHolder, callback);
            }
        });
    }

    private void executeAction(final LibraryDataHolder dataHolder, final BaseAction action, final BaseCallback callback) {
        action.execute(dataHolder, callback);
    }

    private boolean isFinished(final BaseCallback callback) {
        if (actionList.size() <= 0) {
            BaseCallback.invoke(callback, null, null);
            return true;
        }
        return false;
    }
}
