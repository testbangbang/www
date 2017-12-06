package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.ClearAllFreeShapesRequest;
import com.onyx.android.sdk.scribble.utils.NoteViewUtil;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

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
