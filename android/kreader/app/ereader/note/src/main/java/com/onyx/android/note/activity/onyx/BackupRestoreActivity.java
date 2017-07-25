package com.onyx.android.note.activity.onyx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.WifiConnectAction;
import com.onyx.android.note.actions.manager.BackupDataAction;
import com.onyx.android.note.actions.manager.BackupFileNameAction;
import com.onyx.android.note.actions.manager.DownloadFileAction;
import com.onyx.android.note.actions.manager.GetBackupDataAction;
import com.onyx.android.note.actions.manager.LoadLocalBackupFileAction;
import com.onyx.android.note.actions.manager.RestoreDataAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.FileInfo;
import com.onyx.android.sdk.data.model.CloudBackupData;
import com.onyx.android.sdk.data.model.CloudBackupFile;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by ming on 2017/7/18.
 */

public class BackupRestoreActivity extends AppCompatActivity{

    private static final String TAG = "BackupRestoreActivity";
    public final static int SHOW_BACKUP_FILE_COUNT = 8;

    private RecyclerView restoreList;
    private View emptyText;
    private List<FileInfo> localFiles = new ArrayList<>();
    private List<FileInfo> cloudFiles = new ArrayList<>();
    private List<FileInfo> mergeFiles = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        initView();
        registerReceiver();
        checkWifi();
        loadLocalBackupFiles();
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
                prepareBackupFileName(false);
            }
        });
        findViewById(R.id.cloud_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareWifiConnectForCloudBackup();
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
                viewHolder.setText(R.id.name, FileUtils.getBaseName(fileInfo.getName()));
                viewHolder.setText(R.id.size, FileUtils.getFileSize(fileInfo.getSize()));
                viewHolder.setText(R.id.time, DateTimeUtil.formatDate(new Date(fileInfo.getLastModified()), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS));
                viewHolder.setImageResource(R.id.restore, fileInfo.isLocal() ? R.drawable.local_backup : R.drawable.cloud_backup);
                viewHolder.getView(R.id.restore).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String filePath = fileInfo.getPath();
                        if (fileInfo.isLocal()) {
                            restore(filePath);
                        }else {
                            download(filePath, fileInfo.getName());
                        }

                    }
                });
            }

            @Override
            public int getItemCount() {
                return Math.min(mergeFiles.size(), SHOW_BACKUP_FILE_COUNT);
            }
        });
    }

    private void backup(final String fileName, final boolean cloudBackup) {
        new BackupDataAction<BackupRestoreActivity>(cloudBackup, fileName).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String message = e == null ? getString(R.string.backup_success) : e.getMessage();
                Toast.makeText(BackupRestoreActivity.this, message, Toast.LENGTH_SHORT).show();
                loadLocalBackupFiles();
                if (cloudBackup) {
                    loadCloudBackupFiles();
                }
            }
        });
    }

    private void prepareWifiConnectForCloudBackup() {
        if (Device.currentDevice().hasWifi(this) && !NetworkUtil.isWiFiConnected(this)) {
            new WifiConnectAction<>(6000, 1000, getString(R.string.wifi_connecting), true).execute(this, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    prepareBackupFileName(true);
                }
            });
        }else {
            prepareBackupFileName(true);
        }
    }

    private void restore(String filePath) {
        new RestoreDataAction<BackupRestoreActivity>(filePath).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Toast.makeText(BackupRestoreActivity.this, e == null ? R.string.restore_success : R.string.restore_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CloudManager getCloudManager() {
        return NoteApplication.getInstance().getCloudManager();
    }

    private void download(final String filePath, final String fileName) {
        final String savePath = getDir(Constant.CLOUD_BACKUP_FILE_SAVE_FOLDER, Context.MODE_PRIVATE).getAbsolutePath() + "/" + fileName;
        String fileUrl = Constant.ONYX_BACKUP_HOST_BASE + "/" + filePath;
        new DownloadFileAction<>(fileUrl, savePath).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    restore(savePath);
                }else {
                    Toast.makeText(BackupRestoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
        final GetBackupDataAction action = new GetBackupDataAction();
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CloudBackupData cloudBackupData = action.getCloudBackupData();
                if (cloudBackupData == null) {
                    return;
                }
                resolveCloudBackupData(cloudBackupData);
            }
        });
    }

    private void resolveCloudBackupData(CloudBackupData cloudBackupData) {
        cloudFiles.clear();
        List<CloudBackupFile> cloudDataFiles = cloudBackupData.getDataFiles();
        if (cloudDataFiles == null || cloudDataFiles.size() == 0) {
            return;
        }
        for (CloudBackupFile cloudDataFile : cloudDataFiles) {
            FileInfo fileInfo = FileInfo.create(cloudDataFile.getFilename(),
                    cloudDataFile.getSize(),
                    DateTimeUtil.parse(cloudDataFile.getUpdatedAt(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_T_HHMMSS_Z, "GMT"),
                    cloudDataFile.getFile(),
                    false);
            cloudFiles.add(fileInfo);
        }
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

    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                    ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnected()) {
                        loadCloudBackupFiles();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    private void unRegisterReceiver() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void checkWifi() {
        if (Device.currentDevice().hasWifi(this) && !NetworkUtil.isWiFiConnected(BackupRestoreActivity.this.getApplicationContext())) {
            OnyxCustomDialog.getConfirmDialog(this, getString(R.string.wifi_dialog_content), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NetworkUtil.enableWiFi(BackupRestoreActivity.this.getApplicationContext(), true);
                }
            }, null).show();
        }
    }

    private void prepareBackupFileName(final boolean cloudBackup) {
        final BackupFileNameAction action = new BackupFileNameAction(cloudFiles, localFiles, cloudBackup);
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                backup(action.getFileName(), cloudBackup);
            }
        });
    }
}
