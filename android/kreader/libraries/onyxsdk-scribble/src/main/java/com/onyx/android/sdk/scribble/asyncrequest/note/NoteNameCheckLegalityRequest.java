package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;

/**
 * Created by solskjaer49 on 16/7/20 19:31.
 */

public class NoteNameCheckLegalityRequest extends AsyncBaseNoteRequest {
    private String targetName, parentID;
    private boolean checkThisLevelOnly = true;
    private boolean distinguishFileType = true;
    private int currentType;
    private boolean isLegal;

    public boolean isLegal() {
        return isLegal;
    }

    public NoteNameCheckLegalityRequest(String name, String parentID, int curType) {
        this(name, parentID, curType, true, true);
    }

    public NoteNameCheckLegalityRequest(String name, String parentID, int curType,
                                        boolean checkThisLevelOnly, boolean distinguishFileType) {
        this.targetName = name;
        this.checkThisLevelOnly = checkThisLevelOnly;
        this.parentID = parentID;
        this.distinguishFileType = distinguishFileType;
        this.currentType = curType;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(final NoteManager shapeManager) throws Exception {
        isLegal = NoteDataProvider.checkNoteNameLegality(null, targetName, parentID,
                currentType, checkThisLevelOnly, distinguishFileType, false);
    }

}
