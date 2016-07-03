package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageChangeBackgroundRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryCreateRequest;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class ChangeBackgroundAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile int newBackground;

    public ChangeBackgroundAction(final int bg) {
        newBackground = bg;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final PageChangeBackgroundRequest changeRequest = new PageChangeBackgroundRequest(newBackground);
        activity.getNoteViewHelper().submit(activity, changeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(changeRequest, true);
            }
        });
    }
}
