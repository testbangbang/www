package com.onyx.android.sdk.scribble.request.note;

import android.graphics.Bitmap;
import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/22/16.
 */
public class NoteLibrarySaveRequest extends BaseNoteRequest {

    private NoteModel noteModel;
    private Bitmap thumbnail;

    public NoteLibrarySaveRequest(final NoteModel model, final Bitmap t) {
        noteModel = model;
        thumbnail = t;
    }

    public void execute(final ShapeViewHelper shapeManager) throws Exception {
        NoteDataProvider.saveNote(getContext(), noteModel);
        NoteDataProvider.saveThumbnail(getContext(), noteModel.getUniqueId(), thumbnail);
    }


}
