package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryCreateRequest;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class CreateLibraryAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {

    private volatile String parentLibraryId;
    private volatile String title;

    public CreateLibraryAction(final String id, final String t) {
        parentLibraryId = id;
        title = t;
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
    public void execute(final T activity, BaseCallback callback) {
        final NoteLibraryCreateRequest createRequest = new NoteLibraryCreateRequest(parentLibraryId, title);
        activity.submitRequest(createRequest, callback);
    }
}
