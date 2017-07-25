package com.onyx.android.note.actions.manager;

import android.app.Activity;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.FileInfo;
import com.onyx.android.sdk.data.request.data.fs.FileAutoSuffixNameRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/7/25.
 */

public class BackupFileNameAction <T extends Activity> extends BaseNoteAction<T> {

    private List<FileInfo> cloudFiles;
    private List<FileInfo> localFiles;
    private boolean cloudBackup;
    private String fileName;

    public BackupFileNameAction(List<FileInfo> cloudFiles, List<FileInfo> localFiles, boolean cloudBackup) {
        this.cloudFiles = cloudFiles;
        this.localFiles = localFiles;
        this.cloudBackup = cloudBackup;
    }

    @Override
    public void execute(T activity, BaseCallback callback) {
        List<FileInfo> files = cloudBackup ? cloudFiles : localFiles;
        String filePrefix = cloudBackup ? activity.getString(R.string.cloud_note) : activity.getString(R.string.note);
        List<String> fileNames = new ArrayList<>();
        for (FileInfo fileInfo : files) {
            fileNames.add(fileInfo.getBaseName());
        }
        final FileAutoSuffixNameRequest nameRequest = new FileAutoSuffixNameRequest(fileNames, filePrefix);
        getDataManager().submit(activity, nameRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                fileName = nameRequest.getFileName();
            }
        });
    }

    public String getFileName() {
        return fileName;
    }

    private DataManager getDataManager() {
        return NoteApplication.getInstance().getDataManager();
    }
}
