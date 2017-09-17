package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.MirrorSelectedShapeRequest;
import com.onyx.android.sdk.scribble.data.MirrorType;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/9 18:09.
 */

public class MirrorSelectedShapeAction extends BaseNoteAction {

    private MirrorType mirrorType = MirrorType.XAxisMirror;
    private TouchPoint touchPoint;
    private volatile boolean isAddToHistory = false;

    public MirrorSelectedShapeAction(MirrorType type, boolean isAddToHistory) {
        this.mirrorType = type;
        this.isAddToHistory = isAddToHistory;
    }

    public MirrorSelectedShapeAction(TouchPoint touchPoint, boolean isAddToHistory) {
        this.touchPoint = touchPoint;
        this.isAddToHistory = isAddToHistory;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        MirrorSelectedShapeRequest request = new MirrorSelectedShapeRequest(mirrorType, isAddToHistory);
        noteManager.submitRequest(request, callback);
    }
}
