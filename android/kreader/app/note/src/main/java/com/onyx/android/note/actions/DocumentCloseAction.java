package com.onyx.android.note.actions;

import android.os.Bundle;

import com.onyx.android.note.R;
import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.note.dialog.DialogLoading;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentCloseRequest;

/**
 * Created by zhuzeng on 6/29/16.
 */
public class DocumentCloseAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile String title;

    public DocumentCloseAction(final String t) {
        title = t;
    }

    public void execute(final T activity,  final BaseCallback callback) {
        loadingDialog = new DialogLoading();
        Bundle args = new Bundle();
        args.putString(DialogLoading.ARGS_LOADING_MSG, activity.getString(R.string.saving_note));
        loadingDialog.setArguments(args);
        loadingDialog.show(activity.getFragmentManager());
        final NoteDocumentCloseRequest saveRequest = new NoteDocumentCloseRequest(title);
        activity.getNoteViewHelper().submit(activity, saveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loadingDialog.dismiss();
                activity.finish();
            }
        });
    }
}
