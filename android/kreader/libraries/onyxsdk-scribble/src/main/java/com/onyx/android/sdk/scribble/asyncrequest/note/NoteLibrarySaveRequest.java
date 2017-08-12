package com.onyx.android.sdk.scribble.asyncrequest.note;

import android.graphics.Bitmap;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;

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

    @Override
    public void execute(final NoteManager shapeManager) throws Exception {
        NoteDataProvider.saveNote(getContext(), noteModel);
        NoteDataProvider.saveThumbnail(getContext(), noteModel.getUniqueId(), thumbnail);
    }


}
