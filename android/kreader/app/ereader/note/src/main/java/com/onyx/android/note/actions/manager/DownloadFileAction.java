package com.onyx.android.note.actions.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

/**
 * Created by ming on 2017/7/22.
 */

public class DownloadFileAction<T extends Activity> extends BaseNoteAction<T> {

    private DialogProgress dialogProgress;
    private String fileUrl;
    private String savePath;

    private boolean abort = false;

    public DownloadFileAction(String fileUrl, String savePath) {
        this.fileUrl = fileUrl;
        this.savePath = savePath;
    }

    @Override
    public void execute(T activity, BaseCallback callback) {
        initDialogProgress(activity, callback);
        download(activity, callback);
    }

    private void initDialogProgress(final Activity activity, final BaseCallback callback) {
        dialogProgress = new DialogProgress(activity, 0, 100);
        dialogProgress.enableDismissButton(activity.getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abort = true;
                dialogProgress.dismiss();
                BaseCallback.invoke(callback, null, new Exception("abort"));
            }
        }).setTitle(activity.getString(R.string.downloading));
    }

    private void download(Context context, final BaseCallback callback) {
        dialogProgress.show();
        getDownloadManager().download(context, fileUrl, savePath, null, new BaseCallback() {

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                dialogProgress.setProgress((int) info.progress);
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                if (abort) {
                    return;
                }
                dialogProgress.dismiss();
                BaseCallback.invoke(callback, request, e);
            }
        }).start();
    }

    private OnyxDownloadManager getDownloadManager() {
        return NoteApplication.getInstance().getDownloadManager();
    }

}
