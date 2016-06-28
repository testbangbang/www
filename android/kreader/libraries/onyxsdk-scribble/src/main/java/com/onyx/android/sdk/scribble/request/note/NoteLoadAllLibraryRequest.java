package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.List;

/**
 * Created by solskjaer49 on 6/20/16 16:46.
 */
public class NoteLoadAllLibraryRequest extends BaseNoteRequest {

    private List<NoteModel> noteList;

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        noteList = NoteDataProvider.loadAllNoteLibraryList(getContext());
    }

    public List<NoteModel> getNoteList() {
        return noteList;
    }

}
