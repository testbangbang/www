package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by ming on 2017/7/22.
 */

public class CheckNoteModelHasDataRequest extends BaseNoteRequest {

    private boolean hasData = false;

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        hasData = NoteDataProvider.hasData();
        // todo. read data if possible
    }

    public boolean hasData() {
        return hasData;
    }
}
