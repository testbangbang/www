package com.onyx.knote.actions.manager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteMoveRequest;
import com.onyx.knote.actions.BaseNoteAction;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 20:34.
 */

public class NoteMoveAction extends BaseNoteAction {
    private List<String> targetMoveLibraryList;
    private String newParentID;
    private boolean checkNameLegality;
    private boolean checkThisLevelOnly;
    private boolean distinguishFileType;

    public NoteMoveAction(String parentID, List<String> targetMoveLibraryList,
                          boolean checkNameLegality, boolean checkThisLevelOnly, boolean distinguishFileType) {
        this.newParentID = parentID;
        this.targetMoveLibraryList = targetMoveLibraryList;
        this.checkNameLegality = checkNameLegality;
        this.checkThisLevelOnly = checkThisLevelOnly;
        this.distinguishFileType = distinguishFileType;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        final NoteMoveRequest request = new NoteMoveRequest(newParentID, targetMoveLibraryList,
                checkNameLegality, checkThisLevelOnly, distinguishFileType);
        noteManager.submitRequest(request, callback);
    }
}
