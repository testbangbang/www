package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseScribbleRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteLibraryRemoveRequest extends BaseScribbleRequest {

    private String uniqueId;

    public NoteLibraryRemoveRequest(final String id) {
        uniqueId = id;
    }

    public void execute(final ShapeViewHelper shapeManager) throws Exception {
        NoteDataProvider.remove(getContext(), uniqueId);
    }


}
