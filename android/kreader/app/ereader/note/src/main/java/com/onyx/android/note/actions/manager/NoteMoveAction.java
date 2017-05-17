package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteMoveRequest;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 20:34.
 */

public class NoteMoveAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {
    private List<String> targetMoveLibraryList;
    private String newParentID;
    private boolean checkNameLegality;
    private boolean checkThisLevelOnly;
    private boolean distinguishFileType;

    public NoteMoveAction(String parentID, List<String> targetMoveLibraryList, boolean checkNameLegality, boolean checkThisLevelOnly, boolean distinguishFileType) {
        this.newParentID = parentID;
        this.targetMoveLibraryList = targetMoveLibraryList;
        this.checkNameLegality = checkNameLegality;
        this.checkThisLevelOnly = checkThisLevelOnly;
        this.distinguishFileType = distinguishFileType;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.loadNoteList();
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteMoveRequest request = new NoteMoveRequest(newParentID, targetMoveLibraryList,
                checkNameLegality, checkThisLevelOnly, distinguishFileType);
        activity.submitRequest(request, callback);
    }
}
