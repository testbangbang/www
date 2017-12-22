package com.onyx.edu.homework.request;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by lxm on 2017/12/6.
 */

public class DocumentCheckRequest extends BaseNoteRequest {

    private volatile String uniqueId;
    private volatile String parentUniqueId;
    private boolean hasNote = false;

    public DocumentCheckRequest(String uniqueId, String parentUniqueId) {
        this.uniqueId = uniqueId;
        this.parentUniqueId = parentUniqueId;
        setPauseInputProcessor(false);
        setResumeInputProcessor(false);
        setRender(false);
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        super.execute(helper);
        hasNote = NoteDataProvider.checkHasNote(helper.getAppContext(), uniqueId, parentUniqueId);
    }

    public boolean isHasNote() {
        return hasNote;
    }
}
