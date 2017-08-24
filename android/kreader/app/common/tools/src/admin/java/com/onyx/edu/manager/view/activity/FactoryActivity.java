package com.onyx.edu.manager.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.DeviceBind_Table;
import com.onyx.android.sdk.data.request.data.db.TransferDBRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.qrcode.QrCodeActivity;
import com.onyx.android.sdk.qrcode.event.QrCodeEvent;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.DeviceBindAdapter;
import com.onyx.edu.manager.manager.PermissionManager;
import com.onyx.edu.manager.request.DeviceBindExportToCsvRequest;
import com.onyx.edu.manager.request.DeviceBindLoadRequest;
import com.onyx.edu.manager.view.dialog.DialogHolder;
import com.onyx.edu.manager.view.ui.DividerDecoration;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
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
    private MediaPlayer errorPlayer;

    private Set<String> deviceMacList = new HashSet<>();
    private List<DeviceBind> deviceBindList = new ArrayList<>();

    private long countScanned = 0;

    private static String currentBatchTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        ButterKnife.bind(this);

        initConfig();
        initView();
        initData();
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
        toolbar.setTitle(getString(R.string.scan_device_binding));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindListView.setLayoutManager(new LinearLayoutManager(this));
        bindListView.addItemDecoration(new DividerDecoration(this));
        bindListView.setAdapter(new DeviceBindAdapter(deviceBindList));
    }

    private void initData() {
        showBatchSelectDialog();
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

    @OnClick(R.id.btn_scanner)
    public void onDeviceScanClick() {
        Intent intent = QrCodeActivity.createIntent(this, true, 1800);
        intent.putExtra(QrCodeActivity.EXTRA_USE_CUSTOM_BEEP, true);
        startActivityForResult(intent, REQUEST_QR_CODE);
    }

    @OnClick(R.id.btn_select_batch)
    public void onBatchSelectClick() {
        showBatchSelectDialog();
    }

    private File getContentDatabaseFile() {
        return getDatabasePath(ContentDatabase.NAME + ".db");
    }

    public void onDBExportClick() {
        if (!getContentDatabaseFile().exists()) {
            ToastUtils.showToast(getApplicationContext(), R.string.db_file_no_exists);
            return;
        }
        requestExportDbFile();
    }

    private void processDeleteDBTable() {
        Delete.table(DeviceBind.class);
        loadDeviceBindCount(new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ToastUtils.showToast(request.getContext().getApplicationContext(), R.string.db_clear_success);
                countScanned = 0;
                deviceMacList.clear();
                deviceBindList.clear();
                bindListView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void onDBClearClick() {
        DialogHolder.getDialogBaseBuilder(this, getString(R.string.warning),
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String input = dialog.getInputEditText().getText().toString();
                        if (!getString(R.string.confirm_clear).equals(input)) {
                            ToastUtils.showToast(dialog.getContext().getApplicationContext(), R.string.confirm_clear_tip);
                            return;
                        }
                        dialog.dismiss();
                        processDeleteDBTable();
                    }
                })
                .content(R.string.db_clear_warning)
                .input(getString(R.string.confirm_clear_check), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).autoDismiss(false).show();
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
        String errorMessage = null;
        if (deviceBind == null) {
            errorMessage = getString(R.string.scan_code_not_match_warning);
        } else {
            deviceBind.tag = currentBatchTag;
            if (StringUtils.isNullOrEmpty(deviceBind.mac)) {
                errorMessage = getString(R.string.scan_code_mac_empty_warning);
            }
        }
        if (StringUtils.isNotBlank(errorMessage)) {
            playErrorSound();
            ToastUtils.showToast(getApplicationContext(), errorMessage);
            return;
        }
        saveDeviceBind(deviceBind, new ProcessModelTransaction.OnModelProcessListener<DeviceBind>() {
            @Override
            public void onModelProcessed(long current, long total, DeviceBind modifiedModel) {
                if (!isModelModifiedSuccess(modifiedModel)) {
                    ToastUtils.showToast(getApplicationContext(), R.string.device_info_save_fail);
                    return;
                }
                deviceBindInserted(modifiedModel);
            }
        });
    }

    private boolean isModelModifiedSuccess(DeviceBind deviceBind) {
        return deviceBind != null && deviceBind.hasValidId();
    }

    private void deviceBindInserted(DeviceBind deviceBind) {
        if (!deviceMacList.contains(deviceBind.mac)) {
            deviceMacList.add(deviceBind.mac);
            deviceBindList.add(0, deviceBind);
            bindListView.getAdapter().notifyItemInserted(0);
        }
        countScanned = deviceMacList.size();
        updateCountScanned(countScanned);
        ToastUtils.showToast(getApplicationContext(), String.format(getString(R.string.scan_device_success_format),
                countScanned));
        playBeepSound();
    }

    private void playBeepSound() {
        if (beepPlayer == null) {
            beepPlayer = getMediaPlayer(R.raw.beep);
        }
        if (beepPlayer != null) {
            beepPlayer.start();
        }
    }

    private void playErrorSound() {
        if (errorPlayer == null) {
            errorPlayer = getMediaPlayer(R.raw.error);
        }
        if (errorPlayer != null) {
            errorPlayer.start();
        }
    }

    private MediaPlayer getMediaPlayer(int rawRes) {
        MediaPlayer player = null;
        try {
            AssetFileDescriptor file = getResources().openRawResourceFd(rawRes);
            player = new MediaPlayer();
            player.setLooping(false);
            player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            player.setVolume(1f, 1f);
            player.prepare();
            file.close();
        } catch (IOException e) {
        }
        return player;
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
        final DeviceBindLoadRequest countRequest = new DeviceBindLoadRequest(
                ConditionGroup.clause().and(DeviceBind_Table.tag.eq(currentBatchTag)),
                false);
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
        if (queryResult == null) {
            return;
        }
        deviceMacList.clear();
        deviceBindList.clear();
        for (DeviceBind deviceBind : queryResult.getEnsureList()) {
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
        countScannedTv.setText(String.format(getString(R.string.scan_device_before_format), count));
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
                getString(R.string.export_db_permission_deny_rationale), requestCode, list);
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
            EasyPermissions.requestPermissions(this, getString(R.string.export_db_permission_deny_rationale),
                    STORAGE_PERMS_REQUEST_CODE, perms);
        }
    }

    private void afterExportDbPermissionGranted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            DialogHolder.getDialogBaseBuilder(this, null,
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            exportDbFileToSdCard(getExportFilePath());
                        }
                    })
                    .content(getString(R.string.export_file_path_do) +
                            getExportFilePath().replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), ""))
                    .show();
        } else {
            ToastUtils.showToast(getApplicationContext(), R.string.storage_has_no_external);
        }
    }

    private String getExportFilePath() {
        File dir = new File(Environment.getExternalStorageDirectory(), getString(R.string.db_export_dir));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File exportDBFile = new File(dir, ContentDatabase.NAME + "-" +
                new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault()).format(new Date()) + ".db");
        return exportDBFile.getAbsolutePath();
    }

    private void exportDbFileToSdCard(final String exportFilePath) {
        final MaterialDialog dialog = DialogHolder.showProgressDialog(this, getString(R.string.exporting));
        String currentDBPath = getContentDatabaseFile().getAbsolutePath();
        TransferDBRequest restoreDBRequest = new TransferDBRequest(currentDBPath, exportFilePath, false, false, null, null);
        getDataManager().submit(this, restoreDBRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    dialog.dismiss();
                    ToastUtils.showToast(getApplicationContext(), R.string.export_fail);
                    return;
                }
                exportDbFileToCSVFile(exportFilePath, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void exportDbFileToCSVFile(String exportFilePath, final BaseCallback baseCallback) {
        exportFilePath = exportFilePath.replace("db", "csv");
        final DeviceBindExportToCsvRequest exportToCsvRequest = new DeviceBindExportToCsvRequest(exportFilePath);
        getDataManager().submit(getApplicationContext(), exportToCsvRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                boolean result = exportToCsvRequest.isSuccessful();
                ToastUtils.showToast(request.getContext().getApplicationContext(), result ?
                        R.string.export_success : R.string.export_fail);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    private void showBatchSelectDialog() {
        DialogHolder.getDialogBaseBuilder(this, null, null)
                .canceledOnTouchOutside(false)
                .content(R.string.batch_input_content)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.batch_input_hint), currentBatchTag, false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                currentBatchTag = input.toString();
                            }
                        })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadDeviceBindCount(null);
                    }
                })
                .show();
    }
}
