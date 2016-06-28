package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLoadAllLibraryRequest;

/**
 * Created by solskjaer49 on 16/6/28 17:44.
 */

public class NoteLoadAllLibraryAction<T extends ManageActivity> extends BaseNoteAction<T> {
    static final String TAG = NoteLoadAllLibraryAction.class.getSimpleName();

    @Override
    public void execute(final T activity) {
        final NoteLoadAllLibraryRequest loadAllLibraryRequest = new NoteLoadAllLibraryRequest();
        activity.getNoteViewHelper().submit(activity, loadAllLibraryRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.showMoveFolderDialog(loadAllLibraryRequest.getNoteList());
            }
        });
    }
}
