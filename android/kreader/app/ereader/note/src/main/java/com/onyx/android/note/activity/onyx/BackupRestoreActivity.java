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
import com.onyx.android.note.actions.manager.LoadLocalBackupFileAction;
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
import java.util.List;


/**
 * Created by ming on 2017/7/18.
 */

public class BackupRestoreActivity extends AppCompatActivity{

    public final static int SHOW_BACKUP_FILE_COUNT = 8;

    private RecyclerView restoreList;
    private View emptyText;
    private List<FileInfo> localFiles = new ArrayList<>();
    private List<FileInfo> cloudFiles = new ArrayList<>();

    private List<FileInfo> mergeFiles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        initView();
        loadLocalBackupFiles();
        loadCloudBackupFiles();
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
                final FileInfo fileInfo = mergeFiles.get(position);
                File file = new File(fileInfo.getPath());
                viewHolder.setText(R.id.name, FileUtils.getBaseName(fileInfo.getName()));
                viewHolder.setText(R.id.size, FileUtils.getFileSize(file.length()));
                viewHolder.setImageResource(R.id.restore, fileInfo.isLocal() ? R.drawable.local_backup_restore : R.drawable.cloud_backup_restore);
                viewHolder.getView(R.id.restore).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restore(fileInfo.getPath());
                    }
                });
            }

            @Override
            public int getItemCount() {
                return Math.min(mergeFiles.size(), SHOW_BACKUP_FILE_COUNT);
            }
        });
    }

    private void backup(boolean cloudBackup) {
        if (cloudBackup) {
            Toast.makeText(this, "This feature is not yet available", Toast.LENGTH_SHORT).show();
            return;
        }
        new BackupDataAction<BackupRestoreActivity>(cloudBackup).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String message = e == null ? getString(R.string.backup_success) : e.getMessage();
                Toast.makeText(BackupRestoreActivity.this, message, Toast.LENGTH_SHORT).show();
                loadLocalBackupFiles();
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

    public void loadLocalBackupFiles() {
        final LoadLocalBackupFileAction fileAction = new LoadLocalBackupFileAction<>();
        fileAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                localFiles.clear();
                localFiles.addAll(fileAction.getBackupFiles());
                mergeFiles();
            }
        });
    }

    private void loadCloudBackupFiles() {
        cloudFiles.clear();
        mergeFiles();
    }

    private void mergeFiles() {
        mergeFiles.clear();
        mergeFiles.addAll(localFiles);
        mergeFiles.addAll(cloudFiles);
        sortFileList(mergeFiles);
    }

    private void sortFileList(List<FileInfo> backupFiles) {
        if (backupFiles == null) {
            return;
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
        emptyText.setVisibility(mergeFiles.size() == 0 ? View.VISIBLE : View.GONE);
        restoreList.getAdapter().notifyDataSetChanged();
    }


}
