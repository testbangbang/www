package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.UndoRequest;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class UndoAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    public UndoAction() {
    }

    public void execute(final T activity,  final BaseCallback callback) {
        final UndoRequest undoRequest = new UndoRequest();
        activity.getNoteViewHelper().submit(activity, undoRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(undoRequest, true);
            }
        });
    }

}
