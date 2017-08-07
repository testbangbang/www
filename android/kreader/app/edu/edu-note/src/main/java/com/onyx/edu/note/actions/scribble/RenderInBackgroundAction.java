package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.RenderInBackgroundRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

import java.util.List;

/**
 * Created by solskjaer49 on 2017/7/31 17:38.
 */

public class RenderInBackgroundAction extends BaseNoteAction {
    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        List<Shape> shapes = noteManager.detachStash();
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return;
        }
        RenderInBackgroundRequest request = new RenderInBackgroundRequest(shapes);
        noteManager.submitRequest(request, callback);
    }
}
