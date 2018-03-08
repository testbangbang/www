package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.menu.UndoRedoEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.RedoRequest;
import com.onyx.android.sdk.note.request.UndoRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/1.
 */

public class UndoAction extends BaseNoteAction {

    private boolean isLineLayoutMode;

    public UndoAction(NoteManager noteManager, boolean isLineLayoutMode) {
        super(noteManager);
        this.isLineLayoutMode = isLineLayoutMode;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        UndoRequest undoRequest = new UndoRequest(getNoteManager(), isLineLayoutMode);
        getNoteManager().getRxManager().enqueue(undoRequest, new RxCallback<UndoRequest>() {
            @Override
            public void onNext(@NonNull UndoRequest undoRequest) {
                RxCallback.onNext(rxCallback, undoRequest);
                getNoteManager().post(new UndoRedoEvent(true).setRawRenderEnable(false));
            }
        });
    }
}
