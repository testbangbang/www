package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteMoveRequest;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 20:34.
 */

public class NoteMoveAction<T extends ManageActivity> extends BaseNoteAction<T> {
    List<String> targetMoveLibraryList;
    String newParentID;

    public NoteMoveAction(String parentID, List<String> targetMoveLibraryList) {
        this.newParentID = parentID;
        this.targetMoveLibraryList = targetMoveLibraryList;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteMoveRequest request = new NoteMoveRequest(newParentID, targetMoveLibraryList);
        activity.getNoteViewHelper().submit(activity, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.loadNoteList();
            }
        });
    }
}
