package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryLoadRequest;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class GotoUpAction extends BaseNoteAction {

    private volatile String uniqueId;

    public GotoUpAction(final String id) {
        uniqueId = id;
    }

    @Override
    public void execute(final ManageActivity activity) {
        final NoteModel noteModel = NoteDataProvider.load(activity, uniqueId);
        final NoteLibraryLoadRequest loadRequest = new NoteLibraryLoadRequest(noteModel.getParentUniqueId());
        activity.getNoteViewHelper().submit(activity, loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.setCurrentLibraryId(noteModel.getParentUniqueId());
                activity.updateWithNoteList(loadRequest.getNoteList());
            }
        });
    }
}
