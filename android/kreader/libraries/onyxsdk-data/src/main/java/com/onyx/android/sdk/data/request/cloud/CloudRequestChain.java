package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;
import android.util.Pair;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/5/17.
 */

public class CloudRequestChain {
    private boolean abortException = true;

    private List<Pair<BaseCloudRequest, BaseCallback>> pairList = new ArrayList<>();

    public CloudRequestChain addRequest(final BaseCloudRequest request, final BaseCallback callback) {
        addRequestCallBackPair(new Pair<>(request, callback));
        return this;
    }

    public CloudRequestChain addRequestCallBackPair(Pair<BaseCloudRequest, BaseCallback> pair) {
        pairList.add(pair);
        return this;
    }

    public void execute(final Context context, final CloudManager manager) {
        if (isFinished()) {
            return;
        }
        final Pair<BaseCloudRequest, BaseCallback> pair = pairList.remove(0);
        executeRequest(context, manager, pair.first, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(pair.second, request, e);
                if (abortException && e != null) {
                    return;
                }
                execute(context, manager);
            }
        });
    }

    private void executeRequest(final Context context, final CloudManager manager, final BaseCloudRequest request, final BaseCallback callback) {
        manager.submitRequest(context, request, callback);
    }

    public boolean isFinished() {
        return CollectionUtils.isNullOrEmpty(pairList);
    }

    public void clearChainList() {
        if (isFinished()) {
            return;
        }
        pairList.clear();
    }

    public boolean isAbortException() {
        return abortException;
    }

    public CloudRequestChain setAbortException(boolean abort) {
        abortException = abort;
        return this;
    }
}
