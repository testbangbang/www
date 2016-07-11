package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.navigation.PageRemoveRequest;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByPointListRequest;

/**
 * Created by zhuzeng on 7/11/16.
 */
public class RemovePageAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    public RemovePageAction() {
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final PageRemoveRequest removePageRequest = new PageRemoveRequest();
        activity.getNoteViewHelper().submit(activity, removePageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(removePageRequest, true);
            }
        });
    }

}
