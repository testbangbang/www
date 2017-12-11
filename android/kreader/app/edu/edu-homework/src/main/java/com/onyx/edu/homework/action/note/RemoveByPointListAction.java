package com.onyx.edu.homework.action.note;

import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByPointListRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class RemoveByPointListAction extends BaseNoteAction {

    private ShapeRemoveByPointListRequest changeRequest;
    private volatile TouchPointList touchPointList;
    private volatile List<Shape> stash = new ArrayList<>();
    private volatile SurfaceView surfaceView;

    public RemoveByPointListAction(TouchPointList touchPointList, List<Shape> stash, SurfaceView surfaceView) {
        this.touchPointList = touchPointList;
        this.stash = stash;
        this.surfaceView = surfaceView;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        if (touchPointList == null) {
            return;
        }
        changeRequest = new ShapeRemoveByPointListRequest(touchPointList, stash, surfaceView);
        noteViewHelper.submit(getAppContext(), changeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(changeRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
