package com.onyx.kreader.dataprovider.request;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class BaseDataProviderRequest extends BaseRequest {

    public void execute() throws Exception {
    }

    public void afterExecute(final RequestManager requestManager) {
        if (getException() != null) {
            getException().printStackTrace();
        }
        benchmarkEnd();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCallback() != null) {
                    getCallback().done(BaseDataProviderRequest.this, getException());
                }
                requestManager.releaseWakeLock();
            }};

        if (isRunInBackground()) {
            requestManager.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

}
