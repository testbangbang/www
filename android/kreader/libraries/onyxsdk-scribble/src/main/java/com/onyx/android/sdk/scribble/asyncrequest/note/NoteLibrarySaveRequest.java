package com.onyx.android.sdk.scribble.asyncrequest.note;

import android.graphics.Bitmap;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 6/22/16.
 */
public class NoteLibrarySaveRequest extends AsyncBaseNoteRequest {

    private NoteModel noteModel;
    private Bitmap thumbnail;

    public NoteLibrarySaveRequest(final NoteModel model, final Bitmap t) {
        noteModel = model;
        thumbnail = t;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        NoteDataProvider.saveNote(getContext(), noteModel);
        NoteDataProvider.saveThumbnail(getContext(), noteModel.getUniqueId(), thumbnail);
    }


}
