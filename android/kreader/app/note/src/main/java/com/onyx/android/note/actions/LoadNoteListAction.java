package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryLoadRequest;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class LoadNoteListAction<T extends ManageActivity> extends BaseNoteAction<T> {
    private volatile String parentLibraryId;

    public LoadNoteListAction(final String id) {
        parentLibraryId = id;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteModel noteModel = NoteDataProvider.load(activity, parentLibraryId);
        final NoteLibraryLoadRequest loadRequest = new NoteLibraryLoadRequest(parentLibraryId);
        loadRequest.thumbnailLimit = Constant.PERTIME_THUMBNAIL_LOAD_LIMIT;
        activity.getNoteViewHelper().submit(activity, loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.setCurrentLibraryName(noteModel == null ? "" : noteModel.getTitle());
                activity.updateWithNoteList(loadRequest.getNoteList());
            }
        });
    }
}
