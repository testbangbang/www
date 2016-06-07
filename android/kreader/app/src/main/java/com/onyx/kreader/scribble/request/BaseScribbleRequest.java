package com.onyx.kreader.scribble.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.scribble.ShapeManager;

/**
 * Created by zhuzeng on 6/3/16.
 * Requests are used in standalone application or separate page rendering from
 * shape rendering.
 */
public class BaseScribbleRequest extends BaseRequest {

    public void execute(final ShapeManager shapeManager) throws Exception {
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
                    getCallback().done(BaseScribbleRequest.this, getException());
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
