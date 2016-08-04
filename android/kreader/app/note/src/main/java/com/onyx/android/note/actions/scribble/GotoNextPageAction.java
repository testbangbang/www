package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageNextRequest;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoNextPageAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private PageNextRequest nextRequest;

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(nextRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        nextRequest = new PageNextRequest();
        activity.submitRequest(nextRequest, callback);
    }

}
