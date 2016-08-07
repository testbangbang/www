package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageClearRequest;

/**
 * Created by zhuzeng on 8/7/16.
 */
public class ClearPageAction <T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final PageClearRequest pageClearRequest = new PageClearRequest();
        activity.submitRequest(pageClearRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(pageClearRequest, true);
                callback.invoke(callback, request, e);
            }
        });
    }
}
