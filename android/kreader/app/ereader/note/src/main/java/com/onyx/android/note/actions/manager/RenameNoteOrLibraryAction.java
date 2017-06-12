package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteRenameRequest;

/**
 * Created by solskjaer49 on 16/7/21 15:24.
 */

public class RenameNoteOrLibraryAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {
    public RenameNoteOrLibraryAction(String targetId, String newName) {
        this.targetId = targetId;
        this.newName = newName;
    }

    private String targetId;
    private String newName;

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null && !request.isAbort()) {
                    activity.loadNoteList();
                }
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        NoteRenameRequest renameRequest = new NoteRenameRequest(targetId, newName);
        activity.submitRequest(renameRequest, callback);
    }
}
