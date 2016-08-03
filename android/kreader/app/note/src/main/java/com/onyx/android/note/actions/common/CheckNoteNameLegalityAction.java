package com.onyx.android.note.actions.common;

import android.app.Activity;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteNameCheckLegalityRequest;

/**
 * Created by solskjaer49 on 16/7/20 19:30.
 */

public class CheckNoteNameLegalityAction<T extends Activity> extends BaseNoteAction<T> {
    public CheckNoteNameLegalityAction(String targetName) {
        this.targetName = targetName;
    }

    private String targetName;

    public boolean isLegal() {
        return isLegal;
    }

    private boolean isLegal;

    @Override
    public void execute(T activity, final BaseCallback callback) {
        final NoteNameCheckLegalityRequest legalityRequest = new NoteNameCheckLegalityRequest(targetName);
        if (activity instanceof ScribbleActivity) {
            ((ScribbleActivity) activity).getNoteViewHelper().submit(activity, legalityRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    isLegal = legalityRequest.isLegal();
                    callback.done(request, e);
                }
            });
        } else if (activity instanceof ManageActivity) {
            ((ManageActivity) activity).getNoteViewHelper().submit(activity, legalityRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    isLegal = legalityRequest.isLegal();
                    callback.done(request, e);
                }
            });
        }
    }
}
