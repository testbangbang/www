package com.onyx.kreader.ui.actions;

import android.app.Activity;
import android.view.View;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.kreader.R;
import com.onyx.kreader.host.request.ImportReaderScribbleRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;


/**
 * Created by ming on 12/5/16.
 */
public class ImportReaderScribbleAction extends BaseAction {

    private Activity activity;

    public ImportReaderScribbleAction(ReaderDataHolder readerDataHolder) {
        activity = (Activity) readerDataHolder.getContext();
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final DialogProgress progress = new DialogProgress(readerDataHolder.getContext(), 0, 100);
        progress.setTitle(readerDataHolder.getContext().getString(R.string.importing));
        progress.show();

        final ImportReaderScribbleRequest readerScribbleRequest = new ImportReaderScribbleRequest(readerDataHolder);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), readerScribbleRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    int count  = readerScribbleRequest.getImportCount();
                    int max = readerScribbleRequest.getMaxCount();
                    progress.setTitle(readerDataHolder.getContext().getString(R.string.import_success));
                    progress.setSubTitle(readerDataHolder.getContext().getString(R.string.import_success_info, count));
                    progress.setProgress(max);
                    new RefreshCurrentPageAction().execute(readerDataHolder);
                }else {
                    progress.getProgressBar().setVisibility(View.GONE);
                    progress.setTitle(readerDataHolder.getContext().getString(R.string.import_fail));
                }
                BaseCallback.invoke(callback, request, e);
            }

            @Override
            public void progress(BaseRequest request, final ProgressInfo info) {
                super.progress(request, info);
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        progress.setProgress((int) info.progress);
                        progress.setMaxValue((int) info.totalBytes);
                    }
                });
            }
        });
    }

}
