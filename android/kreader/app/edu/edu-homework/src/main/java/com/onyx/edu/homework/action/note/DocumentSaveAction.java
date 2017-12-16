package com.onyx.edu.homework.action.note;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentSaveRequest;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.CloseScribbleEvent;

/**
 * Created by zhuzeng on 6/29/16.
 */
public class DocumentSaveAction extends BaseNoteAction {

    private volatile String title;
    private volatile String documentUniqueId;
    private volatile boolean close;
    private volatile boolean resume = true;
    private Context context;
    private boolean showLoading;

    public DocumentSaveAction(final Context context,
                              final String uniqueId,
                              final String t,
                              boolean c,
                              boolean r,
                              boolean loading) {
        title = t;
        documentUniqueId = uniqueId;
        close = c;
        resume = r;
        this.context = context;
        showLoading = loading;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        if (showLoading) {
            showLoadingDialog(context, R.string.saving);
        }
        final NoteDocumentSaveRequest saveRequest = new NoteDocumentSaveRequest(title, close, resume);
        noteViewHelper.submit(getAppContext(), saveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                if (close) {
                    noteViewHelper.quit();
                    post(new CloseScribbleEvent());
                }
                BaseCallback.invoke(baseCallback, saveRequest, e);
            }
        });
    }
}
