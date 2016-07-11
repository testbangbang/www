package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.shape.PenStateChangeRequest;

/**
 * Created by zhuzeng on 7/9/16.
 */
public class ChangePenStateAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private NoteViewHelper.PenState penState;

    public ChangePenStateAction(NoteViewHelper.PenState state) {
        penState = state;
    }

    public void execute(final T activity,  final BaseCallback callback) {
        final PenStateChangeRequest request = new PenStateChangeRequest(penState);
        activity.getNoteViewHelper().submit(activity, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                callback.invoke(callback, request, e);
            }
        });
    }

}
