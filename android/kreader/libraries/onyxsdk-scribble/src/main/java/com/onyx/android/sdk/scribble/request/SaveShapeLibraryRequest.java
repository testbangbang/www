package com.onyx.android.sdk.scribble.request;

import android.graphics.Bitmap;
import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;

/**
 * Created by zhuzeng on 6/22/16.
 */
public class SaveShapeLibraryRequest extends BaseScribbleRequest {

    private NoteModel noteModel;
    private Bitmap thumbnail;

    public SaveShapeLibraryRequest(final NoteModel model, final Bitmap t) {
        noteModel = model;
        thumbnail = t;
    }

    public void execute(final ShapeViewHelper shapeManager) throws Exception {
        NoteDataProvider.saveNote(getContext(), noteModel);
        NoteDataProvider.saveThumbnail(getContext(), noteModel.getUniqueId(), thumbnail);
    }


}
