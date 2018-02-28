package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.menu.PenWidthChangeEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.PenWidthChangeRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/2/27.
 */

public class PenWidthChangeAction extends BaseNoteAction {

    private float penWidth;

    public PenWidthChangeAction(NoteManager noteManager) {
        super(noteManager);
    }

    public PenWidthChangeAction setPenWidth(float penWidth) {
        this.penWidth = penWidth;
        return this;
    }

    @Override
    public void execute(RxCallback rxCallback) {
        NoteDataBundle.getInstance().getDrawDataHolder().setStrokeWidth(penWidth);
        PenWidthChangeRequest request = new PenWidthChangeRequest(getNoteManager())
                .setPenWidth(penWidth);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<PenWidthChangeRequest>() {
            @Override
            public void onNext(@NonNull PenWidthChangeRequest penWidthChangeRequest) {
                getNoteManager().post(new PenWidthChangeEvent(true));
            }
        });
    }
}
