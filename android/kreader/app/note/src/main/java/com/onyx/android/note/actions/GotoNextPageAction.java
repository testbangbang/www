package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageNextRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoNextPageAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    public GotoNextPageAction() {
    }

    public void execute(final T activity,  final BaseCallback callback) {
        final PageNextRequest nextRequest = new PageNextRequest();
        activity.getNoteViewHelper().submit(activity, nextRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(nextRequest, true);
            }
        });
    }

}
