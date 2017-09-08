package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteLibraryCreateRequest extends AsyncBaseNoteRequest {

    private String parentUniqueId;
    private String title;
    private NoteModel noteModel;

    public NoteLibraryCreateRequest(final String pid, final String t) {
        parentUniqueId = pid;
        title = t;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(final NoteManager shapeManager) throws Exception {
        noteModel = NoteDataProvider.createLibrary(getContext(), ShapeUtils.generateUniqueId(), parentUniqueId, title);
    }

    public NoteModel getNoteModel() {
        return noteModel;
    }

}
