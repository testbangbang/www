package com.onyx.android.note.actions;

import android.os.Bundle;
import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.note.dialog.DialogLoading;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.PenColorChangeRequest;

/**
 * Created by zhuzeng on 7/7/16.
 */
public class ChangePenColorAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private int strokeColor;

    public ChangePenColorAction(int color) {
        strokeColor = color;
    }

    public void execute(final T activity,  final BaseCallback callback) {
        final PenColorChangeRequest request = new PenColorChangeRequest(strokeColor);
        activity.getNoteViewHelper().submit(activity, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                callback.invoke(callback, request, e);
            }
        });
    }



}
