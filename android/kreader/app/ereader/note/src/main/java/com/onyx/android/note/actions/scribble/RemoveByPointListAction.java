package com.onyx.android.note.actions.scribble;

import android.view.SurfaceView;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByPointListRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class RemoveByPointListAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private ShapeRemoveByPointListRequest removeRequest;
    private volatile TouchPointList touchPointList;
    private volatile List<Shape> stash = new ArrayList<>();
    private volatile SurfaceView surfaceView;

    public RemoveByPointListAction(final TouchPointList list, final List<Shape> s, final SurfaceView view) {
        touchPointList = list;
        stash.addAll(s);
        surfaceView = view;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(removeRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        if (touchPointList == null) {
            return;
        }
        removeRequest = new ShapeRemoveByPointListRequest(touchPointList, stash, surfaceView);
        activity.submitRequest(removeRequest, callback);
    }

}
