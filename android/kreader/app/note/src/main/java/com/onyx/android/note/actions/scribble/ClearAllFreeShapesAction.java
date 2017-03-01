package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.ClearAllFreeShapesRequest;

/**
 * Created by zhuzeng on 8/7/16.
 */
public class ClearAllFreeShapesAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final ClearAllFreeShapesRequest clearAllFreeShapesRequest = new ClearAllFreeShapesRequest();
        activity.setFullUpdate(true);
        activity.submitRequest(clearAllFreeShapesRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(clearAllFreeShapesRequest, true);
                callback.invoke(callback, request, e);
            }
        });
    }
}
