package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.AddShapeRequest;
import com.onyx.android.sdk.scribble.request.shape.ChangeCurrentShapeRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 7/12/16.
 */
public class ChangeCurrentShapeAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private int newShape;

    public ChangeCurrentShapeAction(final int shape) {
        newShape = shape;
    }

    public void execute(final T activity, final BaseCallback callback) {
        final ChangeCurrentShapeRequest renderRequest = new ChangeCurrentShapeRequest(newShape);
        activity.getNoteViewHelper().submit(activity, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(renderRequest, true);
            }
        });
    }
}
