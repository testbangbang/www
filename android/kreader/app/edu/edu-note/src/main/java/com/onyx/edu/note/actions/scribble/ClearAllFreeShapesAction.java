package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.ClearAllFreeShapesRequest;
import com.onyx.android.sdk.scribble.utils.NoteViewUtil;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 8/7/16.
 */
public class ClearAllFreeShapesAction extends BaseNoteAction {

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        ClearAllFreeShapesRequest clearAllFreeShapesRequest = new ClearAllFreeShapesRequest();
        NoteViewUtil.setFullUpdate(true);
        noteManager.submitRequest(clearAllFreeShapesRequest, callback);
    }
}
