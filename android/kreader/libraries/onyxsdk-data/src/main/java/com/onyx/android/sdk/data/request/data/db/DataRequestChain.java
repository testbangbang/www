package com.onyx.android.sdk.data.request.data.db;

import android.util.Pair;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 6/2/17.
 */

public class DataRequestChain {

    private List<Pair<BaseDataRequest, BaseCallback>> pairList = new ArrayList<>();

    public DataRequestChain addRequest(final BaseDataRequest request, final BaseCallback callback) {
        pairList.add(new Pair(request, callback));
        return this;
    }

    public void execute(final DataManager dataManager){
        if (isFinished()) {
            return;
        }

        final Pair<BaseDataRequest, BaseCallback> pair = pairList.remove(0);
        executeRequest(dataManager, pair.first, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(pair.second, request, e);
                execute(dataManager);
            }
        });
    }

    private void executeRequest(DataManager dataManager, final BaseDataRequest request, final BaseCallback callback) {
        dataManager.submit(request.getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private boolean isFinished() {
        if (pairList.size() <= 0) {
            return true;
        }
        return false;
    }


}
