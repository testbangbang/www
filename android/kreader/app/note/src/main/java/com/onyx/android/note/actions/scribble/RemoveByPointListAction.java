package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByPointListRequest;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class RemoveByPointListAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private ShapeRemoveByPointListRequest changeRequest;
    private volatile TouchPointList touchPointList;

    public RemoveByPointListAction(final TouchPointList list) {
        touchPointList = list;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(changeRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        if (touchPointList == null) {
            return;
        }
        changeRequest = new ShapeRemoveByPointListRequest(touchPointList);
        activity.submitRequest(changeRequest, callback);
    }

}
