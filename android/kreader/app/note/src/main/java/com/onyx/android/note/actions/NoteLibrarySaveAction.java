package com.onyx.android.note.actions;

import android.graphics.Bitmap;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.note.NoteLibrarySaveRequest;

/**
 * Created by solskjaer49 on 16/6/27 16:36.
 */

public class NoteLibrarySaveAction<T extends ManageActivity> extends BaseNoteAction<T> {
    private volatile NoteModel targetLibraryModel;
    private volatile Bitmap targetThumbnail;

    public NoteLibrarySaveAction(NoteModel targetLibraryModel, Bitmap targetThumbnail) {
        this.targetLibraryModel = targetLibraryModel;
        this.targetThumbnail = targetThumbnail;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteLibrarySaveRequest request = new NoteLibrarySaveRequest(targetLibraryModel, targetThumbnail);
        activity.getNoteViewHelper().submit(activity, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.loadNoteList();
            }
        });
    }
}
