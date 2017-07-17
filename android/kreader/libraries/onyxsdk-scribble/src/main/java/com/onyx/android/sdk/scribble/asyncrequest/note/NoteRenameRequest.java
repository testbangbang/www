package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by solskjaer49 on 16/7/21 15:27.
 */

public class NoteRenameRequest extends AsyncBaseNoteRequest {
    private String parentUniqueId;
    private String noteID;
    private String newName;

    public NoteRenameRequest(String id, String name) {
        noteID = id;
        newName = name;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        NoteDataProvider.renameNote(getContext(), noteID, newName);
    }
}
