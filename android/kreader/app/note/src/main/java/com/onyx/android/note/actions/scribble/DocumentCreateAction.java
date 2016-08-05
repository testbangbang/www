package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.dialog.DialogLoading;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentCreateAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private volatile String uniqueId;
    private volatile String parentUniqueId;
    private NoteDocumentOpenRequest createRequest;

    public DocumentCreateAction(final String id, final String parent) {
        uniqueId = id;
        parentUniqueId = parent;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissLoadingDialog();
                activity.onRequestFinished(createRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        showLoadingDialog(activity, DialogLoading.ARGS_LOADING_MSG, R.string.loading);
        createRequest = new NoteDocumentOpenRequest(uniqueId, parentUniqueId, true);
        activity.submitRequest(createRequest, callback);
    }
}
