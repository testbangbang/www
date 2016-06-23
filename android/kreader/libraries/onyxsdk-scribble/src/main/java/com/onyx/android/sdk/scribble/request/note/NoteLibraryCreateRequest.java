package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteLibraryCreateRequest extends BaseNoteRequest {

    private String parentUniqueId;
    private String title;
    private NoteModel noteModel;

    public NoteLibraryCreateRequest(final String pid, final String t) {
        parentUniqueId = pid;
        title = t;
    }

    public void execute(final ShapeViewHelper shapeManager) throws Exception {
        noteModel = NoteDataProvider.createLibrary(getContext(), parentUniqueId, title);
    }

    public NoteModel getNoteModel() {
        return noteModel;
    }

}
