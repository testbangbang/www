package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.SyncEraseRequest;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/1.
 */

public class SyncEraseAction extends BaseNoteAction {

    private TouchPointList touchPointList;
    private boolean fixShape;

    public SyncEraseAction(NoteManager noteManager, TouchPointList touchPointList) {
        super(noteManager);
        this.touchPointList = touchPointList;
    }

    public SyncEraseAction setFixShape(boolean fixShape) {
        this.fixShape = fixShape;
        return this;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        SyncEraseRequest request = new SyncEraseRequest(getNoteManager(), touchPointList)
                .setFixShape(fixShape);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<SyncEraseRequest>() {
            @Override
            public void onNext(@NonNull SyncEraseRequest syncEraseRequest) {
                RxCallback.onNext(rxCallback, syncEraseRequest);
            }
        });
    }
}
