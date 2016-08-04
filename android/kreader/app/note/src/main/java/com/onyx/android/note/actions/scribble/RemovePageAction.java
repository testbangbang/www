package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageRemoveRequest;

/**
 * Created by zhuzeng on 7/11/16.
 */
public class RemovePageAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private PageRemoveRequest removePageRequest;

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(removePageRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        removePageRequest = new PageRemoveRequest();
        activity.submitRequest(removePageRequest, callback);
    }

}
