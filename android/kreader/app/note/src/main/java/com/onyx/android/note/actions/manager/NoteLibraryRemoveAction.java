package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryRemoveRequest;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/27 19:35.
 */

public class NoteLibraryRemoveAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {
    private List<String> targetRemoveLibraryList;

    public NoteLibraryRemoveAction(List<String> targetRemoveLibraryList) {
        this.targetRemoveLibraryList = targetRemoveLibraryList;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.loadNoteList();
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteLibraryRemoveRequest request = new NoteLibraryRemoveRequest(targetRemoveLibraryList);
        activity.submitRequest(request, callback);
    }
}
