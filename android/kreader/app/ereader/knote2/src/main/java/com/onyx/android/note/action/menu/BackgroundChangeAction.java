package com.onyx.android.note.action.menu;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.PenEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.BackgroundChangeRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/5.
 */

public class BackgroundChangeAction extends BaseNoteAction {

    private int background;

    public BackgroundChangeAction(NoteManager noteManager) {
        super(noteManager);
    }

    public BackgroundChangeAction setBackground(int background) {
        this.background = background;
        return this;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        BackgroundChangeRequest request = new BackgroundChangeRequest(getNoteManager(), background);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<BackgroundChangeRequest>() {
            @Override
            public void onNext(@NonNull BackgroundChangeRequest backgroundChangeRequest) {
                RxCallback.onNext(rxCallback, backgroundChangeRequest);
                getNoteManager().post(PenEvent.resumeDrawingRender());
            }
        });
    }
}
