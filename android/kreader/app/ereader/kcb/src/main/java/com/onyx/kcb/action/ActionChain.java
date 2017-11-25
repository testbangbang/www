package com.onyx.kcb.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.holder.DataBundle;

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

    public void execute(final DataBundle dataHolder, final RxCallback callback) {
        if (isFinished(callback)) {
            return;
        }

        final BaseAction action = actionList.remove(0);
        executeAction(dataHolder, action, new RxCallback() {
            @Override
            public void onNext(Object o) {
                execute(dataHolder, callback);
            }
        });
    }

    private void executeAction(final DataBundle dataHolder, final BaseAction action, final RxCallback callback) {
        action.execute(dataHolder, callback);
    }

    private boolean isFinished(final RxCallback callback) {
        if (actionList.size() <= 0) {
            callback.onComplete();
            return true;
        }
        return false;
    }
}
