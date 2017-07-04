package com.onyx.edu.note.actions.common;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteNameCheckLegalityRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 16/7/20 19:30.
 */

public class CheckNoteNameLegalityAction extends BaseNoteAction {
    public CheckNoteNameLegalityAction(String targetName, String parentID, int curType,
                                       boolean checkThisLevelOnly, boolean distinguishFileType) {
        this.targetName = targetName;
        this.parentID = parentID;
        this.checkThisLevelOnly = checkThisLevelOnly;
        this.distinguishFileType = distinguishFileType;
        this.curType = curType;
    }

    private String targetName;
    private String parentID;
    private boolean checkThisLevelOnly;
    private boolean distinguishFileType;
    private int curType;

    public boolean isLegal() {
        return isLegal;
    }

    private boolean isLegal;

    @Override
    public void execute(NoteManager manager, final BaseCallback callback) {
        final NoteNameCheckLegalityRequest legalityRequest = new NoteNameCheckLegalityRequest(
                targetName, parentID, curType, checkThisLevelOnly, distinguishFileType);
        manager.submitRequest(legalityRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                isLegal = legalityRequest.isLegal();
                callback.done(request, e);
            }
        });
    }
}
