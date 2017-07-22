package com.onyx.android.note.actions.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.request.data.db.DataRequestChain;
import com.onyx.android.sdk.data.request.data.db.TransferDBRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeDatabase;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.NoteRequestChain;
import com.onyx.android.sdk.scribble.request.note.CheckNoteModelHasDataRequest;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.DatabaseUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

import java.io.File;

/**
 * Created by ming on 2017/7/18.
 */

public class RestoreDataAction<T extends Activity> extends BaseNoteAction<T> {

    private final static String TEMP_SHAPE_DATABASE_FILE_NMAE = "shape_temp";

    private String restorePath;

    public RestoreDataAction(String restorePath) {
        this.restorePath = restorePath;
    }

    @Override
    public void execute(T activity, BaseCallback callback) {
        showConfirmDialog(activity, callback);
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

    private void restore(final Context context, final BaseCallback callback) {
        DataRequestChain requestChain = new DataRequestChain();
        requestChain.addRequest(backupTempDBBeforeRestore(context), null);
        requestChain.addRequest(restoreRequest(context), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    checkDBIntegrity(context, e, callback);
                }else {
                    BaseCallback.invoke(callback, request, e);
                }
            }
        });
        requestChain.execute(getDataManager());
    }

    private BaseDataRequest restoreRequest(final Context context){
        return new TransferDBRequest(restorePath, getCurrentDBPath(context), true, true, ShapeGeneratedDatabaseHolder.class);
    }

    private NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getInstance().getNoteViewHelper();
    }

    private String getCurrentDBPath(final Context context) {
        return context.getDatabasePath(ShapeDatabase.NAME).getPath() + ".db";
    }

    private String getTempBackupDBPath(final Context context) {
        String folder = new File(getCurrentDBPath(context)).getParentFile().getAbsolutePath();
        return folder + "/" + TEMP_SHAPE_DATABASE_FILE_NMAE + ".db";
    }

    private BaseDataRequest backupTempDBBeforeRestore(final Context context) {
        return new TransferDBRequest(getCurrentDBPath(context), getTempBackupDBPath(context), false, false, null);
    }

    private void restoreDBAfterFailed(final Context context, final Throwable failRetoreException, final BaseCallback callback) {
        TransferDBRequest dbRequest = new TransferDBRequest(getTempBackupDBPath(context), getCurrentDBPath(context), false, false, null);
        getDataManager().submit(context, dbRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, failRetoreException);
            }
        });
    }

    private void checkDBIntegrity(final Context context, final Throwable failRetoreException, final BaseCallback callback) {
        final CheckNoteModelHasDataRequest hasDataRequest = new CheckNoteModelHasDataRequest();
        getNoteViewHelper().submit(context, hasDataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!hasDataRequest.hasData() || !checkDBVersion(context)) {
                    restoreDBAfterFailed(context, failRetoreException, callback);
                }else {
                    BaseCallback.invoke(callback, request, null);
                }

            }
        });
    }

    private boolean checkDBVersion(final Context context) {
        int restoreDBVersion = DatabaseUtils.getDBVersion(getCurrentDBPath(context));
        return restoreDBVersion <= ShapeDatabase.VERSION;
    }

    private DataManager getDataManager() {
        return NoteApplication.getInstance().getDataManager();
    }
}
