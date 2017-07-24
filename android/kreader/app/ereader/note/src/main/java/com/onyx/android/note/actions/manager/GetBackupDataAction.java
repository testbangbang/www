package com.onyx.android.note.actions.manager;

import android.app.Activity;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CloudBackupData;
import com.onyx.android.sdk.data.request.cloud.GetBackupDataRequest;

/**
 * Created by ming on 2017/7/22.
 */

public class GetBackupDataAction <T extends Activity> extends BaseNoteAction<T> {

    private CloudBackupData cloudBackupData;

    @Override
    public void execute(T activity, final BaseCallback callback) {
        final GetBackupDataRequest backupDataRequest = new GetBackupDataRequest();
        getCloudManager().submitRequest(activity, backupDataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                cloudBackupData = backupDataRequest.getCloudBackupData();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private CloudManager getCloudManager() {
        return NoteApplication.getInstance().getCloudManager();
    }

    public CloudBackupData getCloudBackupData() {
        return cloudBackupData;
    }
}
