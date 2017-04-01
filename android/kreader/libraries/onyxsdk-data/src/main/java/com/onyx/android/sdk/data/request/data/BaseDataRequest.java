package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class BaseDataRequest extends BaseRequest {

    public void execute(final DataManager dataManager) throws Exception {
    }

    public void afterExecute(final DataManager dataManager) {
        if (getException() != null) {
            getException().printStackTrace();
        }
        benchmarkEnd();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCallback() != null) {
                    getCallback().done(BaseDataRequest.this, getException());
                }
                dataManager.getRequestManager().releaseWakeLock();
            }};

        if (isRunInBackground()) {
            dataManager.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public String getIdentifier() {
        return "data";
    }


}
