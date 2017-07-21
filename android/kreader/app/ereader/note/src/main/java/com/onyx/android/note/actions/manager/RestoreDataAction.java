package com.onyx.android.note.actions.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeDatabase;
import com.onyx.android.sdk.scribble.request.note.BackupRestoreDBRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgress;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ming on 2017/7/18.
 */

public class RestoreDataAction<T extends Activity> extends BaseNoteAction<T> {

    private DialogProgress dialogProgress;
    private String restorePath;

    public RestoreDataAction(String restorePath) {
        this.restorePath = restorePath;
    }

    @Override
    public void execute(T activity, BaseCallback callback) {
        initDialogProgress(activity);
        showConfirmDialog(activity, callback);
    }

    private void initDialogProgress(final Activity activity) {
        dialogProgress = new DialogProgress(activity, 0, 100);
        dialogProgress.enableDismissButton(activity.getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogProgress.dismiss();
            }
        }).setTitle(activity.getString(R.string.restoring_tips));
    }

    private void showConfirmDialog(final Activity activity, final BaseCallback callback) {
        OnyxCustomDialog.getConfirmDialog(activity,
                activity.getString(R.string.ask_sure_restore, FileUtils.getBaseName(restorePath)),
                true,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            restore(activity, callback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                null).show();
    }

    private void restore(final Activity activity, final BaseCallback callback){
        Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap = new HashMap<>();
        DatabaseInfo currentDB = DatabaseInfo.create(ShapeDatabase.NAME, ShapeDatabase.VERSION, activity.getDatabasePath(ShapeDatabase.NAME).getPath() + ".db");
        DatabaseInfo newDB = DatabaseInfo.create(restorePath);
        BackupRestoreDBRequest request = new BackupRestoreDBRequest(currentDB, newDB, false);
        getNoteViewHelper().submit(activity, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    initDatabase();
                }
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getInstance().getNoteViewHelper();
    }

    private void initDatabase() {
        FlowManager.destroy();
        NoteApplication.getInstance().initDataProvider();
    }
}
