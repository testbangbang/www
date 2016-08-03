package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageAddRequest;

/**
 * Created by zhuzeng on 6/30/16.
 */
public class DocumentAddNewPageAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private int position;

    public DocumentAddNewPageAction(int pos) {
        position = pos;
    }

    public void execute(final T activity,  final BaseCallback callback) {
        final PageAddRequest pageAddRequest = new PageAddRequest(position);
        activity.getNoteViewHelper().submit(activity, pageAddRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(pageAddRequest, true);
                callback.invoke(callback, request, e);
            }
        });
    }

}
