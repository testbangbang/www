package com.onyx.knote.actions.manager;

import android.content.Context;
import android.view.View;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.note.ImportScribbleRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.knote.R;
import com.onyx.knote.actions.BaseNoteAction;

import java.lang.ref.WeakReference;

/**
 * Created by ming on 2016/12/2.
 */

public class ImportScribbleAction extends BaseNoteAction {
    private WeakReference<Context> contextWeakReference;

    public ImportScribbleAction(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void execute(NoteManager noteManager, final BaseCallback callback) {
        final Context context = contextWeakReference.get();
        if (context == null) {
            return;
        }
        final DialogProgress progress = new DialogProgress(context, 0, 100);
        progress.setTitle(context.getString(R.string.importing));
        progress.show();
        final ImportScribbleRequest scribbleRequest = new ImportScribbleRequest(context);
        noteManager.submitRequest(scribbleRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    int count = scribbleRequest.getImportCount();
                    int max = scribbleRequest.getMaxCount();
                    if (max == 0) {
                        progress.setTitle(context.getString(R.string.no_old_scribble_data));
                        progress.getProgressBar().setVisibility(View.GONE);
                    } else {
                        progress.setTitle(context.getString(R.string.import_success));
                        progress.setSubTitle(context.getString(R.string.import_success_info, count));
                        progress.setProgress(max);
                        progress.setMaxValue(max);
                    }
                } else {
                    progress.getProgressBar().setVisibility(View.GONE);
                    progress.setTitle(context.getString(R.string.import_fail));
                }
                callback.done(request, e);
            }

            @Override
            public void progress(BaseRequest request, final ProgressInfo info) {
                super.progress(request, info);
                progress.setProgress((int) info.progress);
                progress.setMaxValue((int) info.totalBytes);
            }
        });
    }
}
