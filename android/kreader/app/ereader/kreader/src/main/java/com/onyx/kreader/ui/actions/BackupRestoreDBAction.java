package com.onyx.kreader.ui.actions;

import android.database.sqlite.SQLiteDatabase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.data.request.data.db.BackupRestoreDBRequest;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/4/21.
 */

public class BackupRestoreDBAction extends BaseAction {

    private DatabaseInfo currentDB;
    private DatabaseInfo newDB;
    private boolean backup = false;

    public BackupRestoreDBAction(DatabaseInfo currentDB, DatabaseInfo newDB, boolean backup) {
        this.currentDB = currentDB;
        this.newDB = newDB;
        this.backup = backup;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        showLoadingDialog(readerDataHolder, R.string.loading);
        BackupRestoreDBRequest backupRestoreDBRequest = new BackupRestoreDBRequest(currentDB, newDB, backup);
        readerDataHolder.getDataManager().submit(readerDataHolder.getContext(), backupRestoreDBRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
