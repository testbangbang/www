package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryCreateRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryLoadRequest;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class CreateLibraryAction extends BaseNoteAction {

    private volatile String parentLibraryId;
    private volatile String title;

    public CreateLibraryAction(final String id, final String t) {
        parentLibraryId = id;
        title = t;
    }

    @Override
    public void execute(final ManageActivity activity) {
        final NoteLibraryCreateRequest createRequest = new NoteLibraryCreateRequest(parentLibraryId, title);
        activity.getNoteViewHelper().submit(activity, createRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.loadNoteList();
            }
        });
    }
}
