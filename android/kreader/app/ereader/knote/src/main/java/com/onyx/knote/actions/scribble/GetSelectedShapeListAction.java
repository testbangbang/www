package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.GetSelectedShapeListRequest;
import com.onyx.knote.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/9 16:45.
 */

public class GetSelectedShapeListAction extends BaseNoteAction {
    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        noteManager.submitRequest(new GetSelectedShapeListRequest(), callback);
    }
}
