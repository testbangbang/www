package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.GetOldScribbleCountRequest;

/**
 * Created by ming on 2017/3/21.
 */

public class GetOldScribbleCountAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {

    private int count;

    @Override
    public void execute(T activity, final BaseCallback callback) {
        final GetOldScribbleCountRequest oldScribbleCountRequest = new GetOldScribbleCountRequest(activity);
        activity.submitRequest(oldScribbleCountRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                count = oldScribbleCountRequest.getCount();
                callback.done(request, e);
            }
        });
    }

    public int getCount() {
        return count;
    }
}
