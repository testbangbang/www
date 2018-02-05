package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 2018/1/15 19:28.
 */

public class NoteLibraryLoadParentIDRequest extends BaseNoteRequest {
    private volatile String parentUniqueID;
    private volatile String uniqueID;

    public NoteLibraryLoadParentIDRequest(String uniqueID) {
        this.uniqueID = uniqueID;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        NoteModel noteModel = NoteDataProvider.load(uniqueID);
        if (noteModel !=null){
            parentUniqueID = noteModel.getParentUniqueId();
        }
    }

    public String getParentUniqueID() {
        return parentUniqueID;
    }

}
