package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.EraseRequest;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/1.
 */

public class EraseAction extends BaseNoteAction {

    private TouchPointList touchPointList;
    private boolean fixShape;

    public EraseAction(NoteManager noteManager, TouchPointList touchPointList) {
        super(noteManager);
        this.touchPointList = touchPointList;
    }

    public EraseAction setFixShape(boolean fixShape) {
        this.fixShape = fixShape;
        return this;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        EraseRequest request = new EraseRequest(getNoteManager(), touchPointList)
                .setFixShape(fixShape);
        request.setPauseRawDrawingRender(false);
        getNoteManager().getRxManager().enqueue(request, rxCallback);
    }
}
