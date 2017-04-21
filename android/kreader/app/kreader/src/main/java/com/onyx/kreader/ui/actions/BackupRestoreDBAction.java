package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.data.db.BackupRestoreDBRequest;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/4/21.
 */

public class BackupRestoreDBAction extends BaseAction {

    private Map<String, String> backupRestoreDBMap;
    private boolean backup = false;

    public BackupRestoreDBAction(Map<String, String> backupRestoreDBMap, boolean backup) {
        this.backupRestoreDBMap = backupRestoreDBMap;
        this.backup = backup;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        showLoadingDialog(readerDataHolder, R.string.loading);
        BackupRestoreDBRequest backupRestoreDBRequest = new BackupRestoreDBRequest(backupRestoreDBMap, backup);
        readerDataHolder.getDataManager().submit(readerDataHolder.getContext(), backupRestoreDBRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
