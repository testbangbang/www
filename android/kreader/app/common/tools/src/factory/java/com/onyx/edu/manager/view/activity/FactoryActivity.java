package com.onyx.edu.manager.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.request.data.db.TransferDBRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.qrcode.QrCodeActivity;
import com.onyx.android.sdk.qrcode.event.QrCodeEvent;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.PermissionManager;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.DeviceBindAdapter;
import com.onyx.edu.manager.request.DeviceBindLoadRequest;
import com.onyx.edu.manager.view.ui.DividerDecoration;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by suicheng on 2017/6/24.
 */

public class FactoryActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_QR_CODE = 1000;

    private static final int NOTHING_PERMISSIONS_REQUEST = -1000;
    private static final int STORAGE_PERMS_REQUEST_CODE = 1012;
    private static final String[] STORAGE_PERMS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Bind(R.id.bindDevice_list_view)
    RecyclerView bindListView;
    @Bind(R.id.tv_count_scanned)
    TextView countScannedTv;

    private DataManager dataManager;
    private MediaPlayer beepPlayer;

    private Set<String> deviceMacList = new HashSet<>();
    private List<DeviceBind> deviceBindList = new ArrayList<>();

    private long countScanned = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        ButterKnife.bind(this);

        initConfig();
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQrCodeEvent(QrCodeEvent event) {
        processRequestQrCode(event.qrCode);
    }

    private void initData() {
        loadDeviceBindCount(null);
    }

    private void initConfig() {
        try {
            FlowConfig.Builder builder = new FlowConfig.Builder(getApplicationContext());
            FlowManager.init(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("扫码绑定");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindListView.setLayoutManager(new LinearLayoutManager(this));
        bindListView.addItemDecoration(new DividerDecoration(this));
        bindListView.setAdapter(new DeviceBindAdapter(deviceBindList));
    }

    @OnClick(R.id.btn_scanner)
    public void onDeviceScanClick() {
        Intent intent = QrCodeActivity.createIntent(this, true, 1800);
        intent.putExtra(QrCodeActivity.EXTRA_USE_CUSTOM_BEEP, true);
        startActivityForResult(intent, REQUEST_QR_CODE);
    }

    private File getContentDatabaseFile() {
        return getDatabasePath(ContentDatabase.NAME + ".db");
    }

    public void onDBExportClick() {
        if (!getContentDatabaseFile().exists()) {
            ToastUtils.showToast(getApplicationContext(), "数据文件不存在");
            return;
        }
        requestExportDbFile();
    }

    private void processDeleteDBTable() {
        Delete.table(DeviceBind.class);
        loadDeviceBindCount(new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ToastUtils.showToast(request.getContext().getApplicationContext(), "已清空数据库！！");
                countScanned = 0;
                deviceMacList.clear();
                deviceBindList.clear();
                bindListView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void onDBClearClick() {
        MaterialDialog.Builder builder = getAlertDialogBuilder("警告", "此操作会清除掉数据库里所有的记录，请谨慎操作！！",
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String input = dialog.getInputEditText().getText().toString();
                        if (!"确认清空".equals(input)) {
                            ToastUtils.showToast(dialog.getContext().getApplicationContext(),
                                    "请输入\"确认清空\"来防止误操作");
                            return;
                        }
                        dialog.dismiss();
                        processDeleteDBTable();
                    }
                });
        builder.input("输入\"确认清空\"四个字进行校验", null, false, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
            }
        }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).autoDismiss(false).show();
    }

    private MaterialDialog.Builder getAlertDialogBuilder(String title, String content, MaterialDialog.SingleButtonCallback positiveCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColor(Color.GRAY)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(positiveCallback);
        if (StringUtils.isNotBlank(title)) {
            builder.title(title);
        }
        if (StringUtils.isNotBlank(content)) {
            builder.content(content);
        }
        return builder;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_QR_CODE:
                processRequestQrCode(intent);
                break;
        }
    }

    private void processRequestQrCode(Intent intent) {
        if (intent == null) {
            return;
        }
        String qrCode = intent.getStringExtra("qrCode");
        processRequestQrCode(qrCode);
    }

    private void processRequestQrCode(String qrCode) {
        final DeviceBind deviceBind = JSONObjectParseUtils.parseObject(qrCode, DeviceBind.class);
        if (deviceBind == null) {
            ToastUtils.showToast(getApplicationContext(), "扫码失败，设备绑定的信息不对!");
            return;
        }
        saveDeviceBind(deviceBind, new ProcessModelTransaction.OnModelProcessListener<DeviceBind>() {
            @Override
            public void onModelProcessed(long current, long total, DeviceBind modifiedModel) {
                if (!isModelModifiedSuccess(modifiedModel)) {
                    ToastUtils.showToast(getApplicationContext(), "设备信息存储失败");
                    return;
                }
                deviceBindInserted(modifiedModel);
            }
        });
    }

    private boolean isModelModifiedSuccess(DeviceBind deviceBind) {
        if (deviceBind != null && deviceBind.hasValidId()) {
            return true;
        }
        return false;
    }

    private void deviceBindInserted(DeviceBind deviceBind) {
        if (!deviceMacList.contains(deviceBind.mac)) {
            deviceMacList.add(deviceBind.mac);
            deviceBindList.add(0, deviceBind);
            bindListView.getAdapter().notifyItemInserted(0);
        }
        countScanned = deviceMacList.size();
        updateCountScanned(countScanned);
        ToastUtils.showToast(getApplicationContext(), String.format("本次扫码成功，已扫了%d台", countScanned));
        playBeepSound();
    }

    private void playBeepSound() {
        if (beepPlayer == null) {
            try {
                AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
                beepPlayer = new MediaPlayer();
                beepPlayer.setLooping(false);
                beepPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                beepPlayer.setVolume(1f, 1f);
                beepPlayer.prepare();
                file.close();
            } catch (IOException e) {
                beepPlayer = null;
            }
        }
        if (beepPlayer != null) {
            beepPlayer.start();
        }
    }

    private void saveDeviceBind(DeviceBind deviceBind, ProcessModelTransaction.OnModelProcessListener<DeviceBind> listener) {
        ProcessModelTransaction<DeviceBind> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<DeviceBind>() {
                    @Override
                    public void processModel(DeviceBind model) {
                        model.save();
                    }
                }).processListener(listener).add(deviceBind).build();
        Transaction transaction = FlowManager.getDatabase(ContentDatabase.class)
                .beginTransactionAsync(processModelTransaction).build();
        transaction.execute();
    }

    private void loadDeviceBindCount(final BaseCallback baseCallback) {
        final DeviceBindLoadRequest countRequest = new DeviceBindLoadRequest(false);
        getDataManager().submit(this, countRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                updateCountScanned(countRequest.getQueryResult().count);
                updateDeviceListScanned(countRequest.getQueryResult());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void updateDeviceListScanned(final QueryResult<DeviceBind> queryResult) {
        if (queryResult == null || queryResult.isContentEmpty()) {
            return;
        }
        deviceMacList.clear();
        deviceBindList.clear();
        for(DeviceBind deviceBind : queryResult.list) {
            if (!deviceMacList.contains(deviceBind.mac)) {
                deviceMacList.add(deviceBind.mac);
                deviceBindList.add(deviceBind);
            }
        }
        bindListView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_factory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_export_db:
                onDBExportClick();
                return true;
            case R.id.action_clear_db:
                onDBClearClick();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("DefaultLocale")
    private void updateCountScanned(long count) {
        countScannedTv.setText(String.format("已扫描过的设备(不含重复)：%d", count));
    }

    protected int getPermissionRequestCode() {
        return 1;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (getPermissionRequestCode() == NOTHING_PERMISSIONS_REQUEST) {
            Toast.makeText(getApplicationContext(), "Permission RequestCode must be override",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        PermissionManager.processPermissionPermanentlyDenied(FactoryActivity.this,
                "db文件导出到sd卡，需要获取读取外部存储卡的权限",
                requestCode, list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(STORAGE_PERMS_REQUEST_CODE)
    private void requestExportDbFile() {
        String[] perms = STORAGE_PERMS;
        if (EasyPermissions.hasPermissions(this, perms)) {
            afterExportDbPermissionGranted();
        } else {
            EasyPermissions.requestPermissions(this, "导出db文件，需要申请读取存储卡的权限",
                    STORAGE_PERMS_REQUEST_CODE, perms);
        }
    }

    private void afterExportDbPermissionGranted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            getAlertDialogBuilder(null, "导出的路径：" + getExportFilePath().replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), ""),
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            exportDbFileToSdCard(getExportFilePath());
                        }
                    }).show();
        } else {
            ToastUtils.showToast(getApplicationContext(), "没有外部存储");
        }
    }

    private String getExportFilePath() {
        File dir = new File(Environment.getExternalStorageDirectory(), "Onyx工厂管理/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File exportDBFile = new File(dir, ContentDatabase.NAME + "-" +
                new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault()).format(new Date()) +
                ".db");
        return exportDBFile.getAbsolutePath();
    }

    private void exportDbFileToSdCard(String exportFilePath) {
        String currentDBPath = getContentDatabaseFile().getAbsolutePath();
        TransferDBRequest restoreDBRequest = new TransferDBRequest(currentDBPath, exportFilePath, false, false, null);
        getDataManager().submit(this, restoreDBRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ToastUtils.showToast(request.getContext().getApplicationContext(), e != null ? "导出失败" : "导出成功");
            }
        });
    }

    private DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }
}
