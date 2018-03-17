package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.RemoveShapesByGroupIdRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/17.
 */

public class RemoveShapesByGroupIdAction extends BaseNoteAction {

    private String groupId;

    public RemoveShapesByGroupIdAction(NoteManager noteManager, String groupId) {
        super(noteManager);
        this.groupId = groupId;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        RemoveShapesByGroupIdRequest request = new RemoveShapesByGroupIdRequest(getNoteManager(), groupId);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<RemoveShapesByGroupIdRequest>() {
            @Override
            public void onNext(@NonNull RemoveShapesByGroupIdRequest removeShapesByGroupIdRequest) {
                RxCallback.onNext(rxCallback, removeShapesByGroupIdRequest);
            }
        });
    }
}
