package com.onyx.android.note.actions.manager;

import android.app.Activity;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.FileInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.note.CollectFilesRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ming on 2017/7/21.
 */

public class LoadLocalBackupFileAction<T extends Activity> extends BaseNoteAction<T> {

    private List<FileInfo> backupFiles;

    public LoadLocalBackupFileAction() {
    }

    @Override
    public void execute(T activity, final BaseCallback callback) {
        backupFiles = new ArrayList<>();
        Set<String> filter = new HashSet<>();
        filter.add("db");
        final Set<String> fileList = new HashSet<>();
        final CollectFilesRequest fileRequest = new CollectFilesRequest(BackupDataAction.BACKUP_LOCAL_SAVE_PATH, filter, false, fileList);
        getNoteViewHelper().submit(activity, fileRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                for (String s : fileList) {
                    File file = new File(s);
                    backupFiles.add(FileInfo.create(FileUtils.getBaseName(s), file.length(), file.lastModified(), s, true));
                }
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public List<FileInfo> getBackupFiles() {
        return backupFiles;
    }

    private NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getInstance().getNoteViewHelper();
    }
}
