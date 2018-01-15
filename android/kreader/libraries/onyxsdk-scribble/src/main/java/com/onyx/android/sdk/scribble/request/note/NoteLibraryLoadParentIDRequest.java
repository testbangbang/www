package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 2018/1/15 19:28.
 */

public class NoteLibraryLoadParentIDRequest extends BaseNoteRequest {
    public String getParentUniqueID() {
        return parentUniqueID;
    }

    private String parentUniqueID;

    public NoteLibraryLoadParentIDRequest(String uniqueID) {
        this.uniqueID = uniqueID;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    private String uniqueID;

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        parentUniqueID = NoteDataProvider.load(uniqueID).getParentUniqueId();
    }
}
