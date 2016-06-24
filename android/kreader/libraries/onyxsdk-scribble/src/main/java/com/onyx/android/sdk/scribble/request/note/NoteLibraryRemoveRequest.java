package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteLibraryRemoveRequest extends BaseNoteRequest {

    private String uniqueId;

    public NoteLibraryRemoveRequest(final String id) {
        uniqueId = id;
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        NoteDataProvider.remove(getContext(), uniqueId);
    }


}
