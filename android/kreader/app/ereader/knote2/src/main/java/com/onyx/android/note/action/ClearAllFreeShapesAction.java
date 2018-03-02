package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.ClearAllFreeShapesEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.ClearAllFreeShapesRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/1.
 */

public class ClearAllFreeShapesAction extends BaseNoteAction {

    public ClearAllFreeShapesAction(NoteManager noteManager) {
        super(noteManager);
    }

    @Override
    public void execute(RxCallback rxCallback) {
        ClearAllFreeShapesRequest request = new ClearAllFreeShapesRequest(getNoteManager());
        getNoteManager().getRxManager().enqueue(request, new RxCallback<ClearAllFreeShapesRequest>() {
            @Override
            public void onNext(@NonNull ClearAllFreeShapesRequest clearAllFreeShapesRequest) {
                getNoteManager().post(new ClearAllFreeShapesEvent(true));
            }
        });
    }
}
