package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryLoadRequest;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class LoadNoteListAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {
    private volatile String parentLibraryId;
    private NoteModel noteModel;
    private NoteLibraryLoadRequest loadRequest;

    public LoadNoteListAction(final String id) {
        parentLibraryId = id;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.updateCurLibName(noteModel == null ? "" : noteModel.getTitle());
                activity.updateUIWithNewNoteList(loadRequest.getNoteList());
            }
        });
    }


    @Override
    public void execute(final T activity, BaseCallback callback) {
        noteModel = NoteDataProvider.load(activity, parentLibraryId);
        loadRequest = new NoteLibraryLoadRequest(parentLibraryId);
        loadRequest.thumbnailLimit = Constant.PERTIME_THUMBNAIL_LOAD_LIMIT;
        activity.submitRequest(loadRequest, callback);
    }
}
