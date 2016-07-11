package com.onyx.android.note.actions;

import android.util.Log;
import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByPointListRequest;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class RemoveByPointListAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile TouchPointList touchPointList;

    public RemoveByPointListAction(final TouchPointList list) {
        touchPointList = list;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        if (touchPointList == null) {
            return;
        }
        final ShapeRemoveByPointListRequest changeRequest = new ShapeRemoveByPointListRequest(touchPointList);
        activity.getNoteViewHelper().submit(activity, changeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(changeRequest, true);
            }
        });
    }

}
