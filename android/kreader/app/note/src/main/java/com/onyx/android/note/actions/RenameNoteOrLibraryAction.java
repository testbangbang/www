package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteRenameRequest;

/**
 * Created by solskjaer49 on 16/7/21 15:24.
 */

public class RenameNoteOrLibraryAction<T extends ManageActivity> extends BaseNoteAction<T> {
    public RenameNoteOrLibraryAction(String targetId, String newName) {
        this.targetId = targetId;
        this.newName = newName;
    }

    private String targetId;
    private String newName;

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        NoteRenameRequest renameRequest = new NoteRenameRequest(targetId, newName);
        activity.getNoteViewHelper().submit(activity, renameRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null && !request.isAbort()) {
                    activity.loadNoteList();
                }
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
