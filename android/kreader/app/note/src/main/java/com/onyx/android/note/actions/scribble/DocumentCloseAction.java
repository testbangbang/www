package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.dialog.DialogLoading;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentCloseRequest;

/**
 * Created by zhuzeng on 6/29/16.
 */
public class DocumentCloseAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private NoteDocumentCloseRequest closeRequest;
    private volatile String title;
    private volatile String documentUniqueId;

    public DocumentCloseAction(final String uniqueId, final String t) {
        title = t;
        documentUniqueId = uniqueId;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissLoadingDialog();
                activity.finish();
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        showLoadingDialog(activity, DialogLoading.ARGS_LOADING_MSG, R.string.saving_note);
        final NoteDocumentCloseRequest closeRequest = new NoteDocumentCloseRequest(title);
        activity.submitRequestWithIdentifier(documentUniqueId,
                closeRequest, callback);
    }
}
