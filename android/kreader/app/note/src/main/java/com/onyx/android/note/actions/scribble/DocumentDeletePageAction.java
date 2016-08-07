package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageRemoveRequest;

/**
 * Created by zhuzeng on 6/30/16.
 */
public class DocumentDeletePageAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {


    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final PageRemoveRequest pageRemoveRequest = new PageRemoveRequest();
        activity.submitRequest(pageRemoveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(pageRemoveRequest, true);
                callback.invoke(callback, request, e);
            }
        });
    }

}
