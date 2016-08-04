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

    public NoteMoveAction(String parentID, List<String> targetMoveLibraryList) {
        this.newParentID = parentID;
        this.targetMoveLibraryList = targetMoveLibraryList;
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
        final NoteMoveRequest request = new NoteMoveRequest(newParentID, targetMoveLibraryList);
        activity.submitRequest(request, callback);
    }
}
