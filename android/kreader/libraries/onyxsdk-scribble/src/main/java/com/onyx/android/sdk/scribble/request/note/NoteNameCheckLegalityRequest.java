package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 16/7/20 19:31.
 */

public class NoteNameCheckLegalityRequest extends BaseNoteRequest {
    private String targetName, parentID;
    boolean checkThisLevelOnly = true;
    private boolean isLegal;

    public boolean isLegal() {
        return isLegal;
    }

    public NoteNameCheckLegalityRequest(String name, String parentID) {
        this(name, parentID, true);
    }

    public NoteNameCheckLegalityRequest(String name, String parentID, boolean checkThisLevelOnly) {
        this.targetName = name;
        this.checkThisLevelOnly = checkThisLevelOnly;
        this.parentID = parentID;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper shapeManager) throws Exception {
        isLegal = NoteDataProvider.checkNoteNameLegality(targetName, parentID, checkThisLevelOnly);
    }

}
