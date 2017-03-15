package com.onyx.android.note.actions.manager;

import android.view.View;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.request.note.ImportScribbleRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;

import java.util.List;

/**
 * Created by ming on 2016/12/2.
 */

public class ImportScribbleAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final DialogProgress progress = new DialogProgress(activity, 0, 100);
        progress.setTitle(activity.getString(R.string.importing));
        progress.show();
        final ImportScribbleRequest scribbleRequest = new ImportScribbleRequest(activity);
        activity.submitRequest(scribbleRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    int count  = scribbleRequest.getImportCount();
                    int max = scribbleRequest.getMaxCount();
                    if (max == 0) {
                        progress.setTitle(activity.getString(R.string.no_old_scribble_data));
                        progress.getProgressBar().setVisibility(View.GONE);
                    }else {
                        progress.setTitle(activity.getString(R.string.import_success));
                        progress.setSubTitle(activity.getString(R.string.import_success_info, count));
                        progress.setProgress(max);
                        progress.setMaxValue(max);
                    }
                }else {
                    progress.getProgressBar().setVisibility(View.GONE);
                    progress.setTitle(activity.getString(R.string.import_fail));
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
