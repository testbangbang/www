package com.onyx.edu.homework.base;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class NoteActionChain {

    private List<BaseNoteAction> actionList = new ArrayList<>();
    private boolean abortWhenException = false;

    public NoteActionChain() {
    }

    public NoteActionChain(boolean abortWhenException) {
        this.abortWhenException = abortWhenException;
    }

    public NoteActionChain addAction(final BaseNoteAction action) {
        actionList.add(action);
        return this;
    }

    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback callback) {
        final BaseNoteAction action = actionList.remove(0);
        executeAction(noteViewHelper, action, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (isFinished(callback, request, e)) {
                    return;
                }
                execute(noteViewHelper, callback);
            }
        });
    }

    private void executeAction(final NoteViewHelper noteViewHelper, final BaseNoteAction action, final BaseCallback callback) {
        action.execute(noteViewHelper, callback);
    }

    private boolean isFinished(final BaseCallback callback, final BaseRequest request, final Throwable e) {
        if (actionList.size() <= 0 || (abortWhenException && e != null)) {
            BaseCallback.invoke(callback, request, e);
            return true;
        }
        return false;
    }

}
