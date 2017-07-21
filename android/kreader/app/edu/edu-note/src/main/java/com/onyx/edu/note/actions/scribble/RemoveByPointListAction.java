package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeRemoveByPointListRequest;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class RemoveByPointListAction extends BaseNoteAction {
    private volatile TouchPointList touchPointList;

    public RemoveByPointListAction(final TouchPointList list) {
        touchPointList = list;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        if (touchPointList == null) {
            return;
        }
        ShapeRemoveByPointListRequest changeRequest = new ShapeRemoveByPointListRequest(touchPointList);
        noteManager.submitRequest(changeRequest, callback);
    }
}
