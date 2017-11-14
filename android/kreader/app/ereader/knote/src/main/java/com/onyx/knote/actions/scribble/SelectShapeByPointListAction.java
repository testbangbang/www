package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.SelectShapeByPointListRequest;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/1 16:16.
 */

public class SelectShapeByPointListAction extends BaseNoteAction {
    private volatile TouchPointList touchPointList;

    public SelectShapeByPointListAction(final TouchPointList list) {
        touchPointList = list;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        if (touchPointList == null) {
            return;
        }
        SelectShapeByPointListRequest changeRequest = new SelectShapeByPointListRequest(touchPointList);
        noteManager.submitRequest(changeRequest, callback);
    }
}
