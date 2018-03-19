package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.PenEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.RedoRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/1.
 */

public class RedoAction extends BaseNoteAction {

    public RedoAction(NoteManager noteManager) {
        super(noteManager);
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        RedoRequest redoRequest = new RedoRequest(getNoteManager());
        getNoteManager().getRxManager().enqueue(redoRequest, new RxCallback<RedoRequest>() {
            @Override
            public void onNext(@NonNull RedoRequest redoRequest) {
                RxCallback.onNext(rxCallback, redoRequest);
                getNoteManager().post(PenEvent.resumeDrawingRender());
            }
        });
    }
}
