package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.mx.ScribbleActivity;
import com.onyx.android.note.dialog.DialogLoading;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentEditAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile String uniqueId;
    private volatile String parentUniqueId;

    public DocumentEditAction(final String id, final String parentId) {
        uniqueId = id;
        parentUniqueId = parentId;
    }

    public void execute(final T activity , final BaseCallback callback) {
        showLoadingDialog(activity, DialogLoading.ARGS_LOADING_MSG, R.string.loading);
        final NoteDocumentOpenRequest openRequest = new NoteDocumentOpenRequest(uniqueId, parentUniqueId, false);
        activity.getNoteViewHelper().submitRequestWithIdentifier(activity, uniqueId, openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissLoadingDialog();
                activity.onRequestFinished(openRequest, true);
                callback.invoke(callback, request, e);
            }
        });
    }
}

