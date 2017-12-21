package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.dialog.DialogLoading;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentSaveRequest;

/**
 * Created by zhuzeng on 6/29/16.
 */
public class DocumentSaveAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private volatile String title;
    private volatile String documentUniqueId;
    private volatile boolean close;
    private volatile boolean resume = true;

    public DocumentSaveAction(final String uniqueId, final String t, boolean c) {
        this(uniqueId, t, c, true);
    }

    public DocumentSaveAction(final String uniqueId, final String t, boolean c, boolean r) {
        title = t;
        documentUniqueId = uniqueId;
        close = c;
        resume = r;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        showLoadingDialog(activity, DialogLoading.ARGS_LOADING_MSG, R.string.saving_note);
        final NoteDocumentSaveRequest saveRequest = new NoteDocumentSaveRequest(title, close, resume, true, true);
        activity.submitRequest(saveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissLoadingDialog();
                if (close) {
                    activity.getNoteViewHelper().quit();
                    activity.finish();
                }
                BaseCallback.invoke(callback, saveRequest, e);
            }
        });
    }
}
