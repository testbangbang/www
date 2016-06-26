package com.onyx.android.note.actions;

import android.widget.BaseAdapter;
import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryLoadRequest;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class LoadNoteListAction extends BaseNoteAction {
    private volatile String parentLibraryId;

    public LoadNoteListAction(final String id) {
        parentLibraryId = id;
    }

    @Override
    public void execute(final ManageActivity activity) {
        final NoteLibraryLoadRequest loadRequest = new NoteLibraryLoadRequest(parentLibraryId);
        activity.getNoteViewHelper().submit(activity, loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.updateWithNoteList(loadRequest.getNoteList());
            }
        });
    }
}
