package com.onyx.android.note.actions.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.cloud.UploadBackupFileRequest;
import com.onyx.android.sdk.data.request.data.db.TransferDBRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeDatabase;
import com.onyx.android.sdk.scribble.request.note.CheckNoteModelHasDataRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Date;

/**
 * Created by ming on 2017/7/18.
 */

public class BackupDataAction<T extends Activity> extends BaseNoteAction<T> {

    public final static String BACKUP_LOCAL_SAVE_PATH = "mnt/sdcard/note/backup/local/";

    private DialogProgress dialogProgress;
    private boolean cloudBackup = false;

    public BackupDataAction(boolean cloudBackup) {
        this.cloudBackup = cloudBackup;
    }

    @Override
    public void execute(T activity, BaseCallback callback) {
        checkDBHasData(activity, callback);
    }

    private void checkDBHasData(final Context context, final BaseCallback callback) {
        final CheckNoteModelHasDataRequest hasDataRequest = new CheckNoteModelHasDataRequest();
        getNoteViewHelper().submit(context, hasDataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!hasDataRequest.hasData()) {
                    BaseCallback.invoke(callback, request, new Throwable(context.getString(R.string.has_no_data)));
                }else {
                    showTitleDialog(context, callback);
                }

            }
        });
    }

    private void showTitleDialog(final Context context, final BaseCallback callback) {
        final String title = DateTimeUtil.formatDate(new Date(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS_FOR_FILE_NAME);
        OnyxCustomDialog.getInputDialog(context, context.getString(R.string.backup_file_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OnyxCustomDialog customDialog = (OnyxCustomDialog)dialog;
                String input = customDialog.getInputValue().toString();
                backup(context, StringUtils.isNullOrEmpty(input) ? title : input, callback);
            }
        }).setInputHintText(title).show();
    }

    private void showDialogProgress(final Context context) {
        dialogProgress = new DialogProgress(context, 0, 100);
        dialogProgress.enableDismissButton(context.getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogProgress.dismiss();
            }
        }).setTitle(context.getString(R.string.uploading)).show();
    }

    private void showConfirmDialog(final Context context) {
        OnyxCustomDialog.getConfirmDialog(context,
                context.getString(R.string.no_finish_backup_tips),
                true,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogProgress.dismiss();
                    }
                },
                null).show();
    }

    private void backup(final Context context, final String fileName, final BaseCallback callback) {
        final String backupDBPath = getBackupDBPath(fileName);
        String currentDBPath = context.getDatabasePath(ShapeDatabase.NAME).getPath() + ".db";
        TransferDBRequest request = new TransferDBRequest(currentDBPath, backupDBPath, false, false, ShapeDatabase.class, null);
        getDataManager().submit(context, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (cloudBackup && e == null) {
                    upload(context, callback, backupDBPath);
                }else {
                    BaseCallback.invoke(callback, request, e);
                }
            }
        });
    }

    private String getBackupDBPath(final String fileName) {
        return BACKUP_LOCAL_SAVE_PATH + fileName + ".db";
    }

    private NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getInstance().getNoteViewHelper();
    }

    private CloudManager getCloudManager() {
        return NoteApplication.getInstance().getCloudManager();
    }

    private void upload(final Context context,
                        final BaseCallback callback,
                        final String filePath) {
        showDialogProgress(context);
        UploadBackupFileRequest uploadBackupFileRequest = new UploadBackupFileRequest(filePath, true);
        getCloudManager().submitRequest(context, uploadBackupFileRequest, new BaseCallback() {

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                dialogProgress.setProgress((int) info.progress);
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogProgress.dismiss();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private DataManager getDataManager() {
        return NoteApplication.getInstance().getDataManager();
    }
}
