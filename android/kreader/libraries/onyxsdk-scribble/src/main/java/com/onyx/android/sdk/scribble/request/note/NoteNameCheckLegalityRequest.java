package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 16/7/20 19:31.
 */

public class NoteNameCheckLegalityRequest extends BaseNoteRequest {
    String targetName;

    public boolean isLegal() {
        return isLegal;
    }

    boolean isLegal;

    public NoteNameCheckLegalityRequest(String name) {
        this.targetName = name;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        isLegal = NoteDataProvider.checkNoteNameLegality(targetName);
    }

}
