package com.onyx.android.note.activity.onyx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.manager.BackupDataAction;
import com.onyx.android.note.actions.manager.RestoreDataAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.FileInfo;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.onyx.android.note.actions.manager.BackupDataAction.BACKUP_CLOUD_SAVE_PATH;
import static com.onyx.android.note.actions.manager.BackupDataAction.BACKUP_LOCAL_SAVE_PATH;

/**
 * Created by ming on 2017/7/18.
 */

public class BackupRestoreActivity extends AppCompatActivity{

    public final static int SHOW_BACKUP_FILE_COUNT = 8;

    private RecyclerView restoreList;
    private View emptyText;
    private List<FileInfo> backupFiles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        initView();
        loadBackupFiles();
    }

    private void initView() {
        emptyText = findViewById(R.id.empty_text);
        restoreList = (RecyclerView) findViewById(R.id.restore_list);
        findViewById(R.id.top_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.local_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup(false);
            }
        });
        findViewById(R.id.cloud_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup(true);
            }
        });
        initBackupFileList();
    }

    private void initBackupFileList() {
        restoreList.setLayoutManager(new LinearLayoutManager(this));
        restoreList.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.backup_file_list_item, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                final FileInfo fileInfo = backupFiles.get(position);
                File file = new File(fileInfo.getPath());
                viewHolder.setText(R.id.name, FileUtils.getBaseName(fileInfo.getName()));
                viewHolder.setText(R.id.size, FileUtils.getFileSize(file.length()));
                String parentFolder = FileUtils.getBaseName(new File(fileInfo.getPath()).getParentFile().getName());
                viewHolder.setImageResource(R.id.restore, isLocalBackupFile(parentFolder) ? R.drawable.local_backup_restore : R.drawable.cloud_backup_restore);
                viewHolder.getView(R.id.restore).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restore(fileInfo.getPath());
                    }
                });
            }

            @Override
            public int getItemCount() {
                return Math.min(backupFiles.size(), SHOW_BACKUP_FILE_COUNT);
            }
        });
    }

    private boolean isLocalBackupFile(String parentFolder) {
        return parentFolder.startsWith(BackupDataAction.LOCAL_FOLDER);
    }

    private void backup(boolean cloudBackup) {
        new BackupDataAction<BackupRestoreActivity>(cloudBackup).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Toast.makeText(BackupRestoreActivity.this, e == null ? R.string.backup_success : R.string.backup_fail, Toast.LENGTH_SHORT).show();
                loadBackupFiles();
            }
        });
    }

    private void restore(String filePath) {
        new RestoreDataAction<BackupRestoreActivity>(filePath).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Toast.makeText(BackupRestoreActivity.this, e == null ? R.string.restore_success : R.string.restore_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadBackupFiles() {
        backupFiles.clear();
        Set<String> filter = new HashSet<>();
        filter.add("db");
        List<String> fileList = new ArrayList<>();
        FileUtils.collectFiles(BACKUP_LOCAL_SAVE_PATH, filter, false, fileList);
        FileUtils.collectFiles(BACKUP_CLOUD_SAVE_PATH, filter, false, fileList);
        for (String s : fileList) {
            backupFiles.add(FileInfo.create(s, new File(s).lastModified(), s));
        }
        Collections.sort(backupFiles, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo file1, FileInfo file2) {
                if (file1.getLastModified() > file2.getLastModified()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        emptyText.setVisibility(backupFiles.size() == 0 ? View.VISIBLE : View.GONE);
        restoreList.getAdapter().notifyDataSetChanged();
    }

}
