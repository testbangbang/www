package com.onyx.android.libsetting.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityFirmwareOtaBinding;
import com.onyx.android.libsetting.util.DeviceFeatureUtil;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareLocalCheckLegalityRequest;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

public class FirmwareOTAActivity extends OnyxAppCompatActivity {
    public static final String ACTION_OTA_DOWNLOAD = "com.action.ota.download";
    private static final String NEW_LINE = "\n";
    private ActivityFirmwareOtaBinding binding;
    private DeviceReceiver receiver = new DeviceReceiver();
    private boolean otaGuard = false;

    private OnyxAlertDialog checkingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDeviceReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!NetworkHelper.isWifiEnable(this)) {
            receiver.enable(this, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        receiver.enable(this, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAllUpdate();
    }

    private void initDeviceReceiver() {
        receiver.setWifiStateListener(new DeviceReceiver.WifiStateListener() {
            @Override
            public void onWifiStateChanged(Intent intent) {
            }

            @Override
            public void onWifiConnected(Intent intent) {
                checkAllUpdate();
            }
        });
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_firmware_ota);
        initSupportActionBarWithCustomBackFunction();
        binding.buttonCheckOta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllUpdate();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.ota_info_preference,
                new OTASettingPreferenceFragment()).commit();
    }

    private void processIntent() {
        String action = getIntent().getAction();
        if (ACTION_OTA_DOWNLOAD.equals(action)) {
            checkUpdateFromCloud();
        }
    }

    private void checkNetworkForCloudUpdate() {
        if (!DeviceFeatureUtil.hasWifi(this)) {
            return;
        }
        if (!NetworkHelper.isWifiConnected(FirmwareOTAActivity.this)) {
            NetworkHelper.enableWifi(FirmwareOTAActivity.this, true);
            showToast(R.string.opening_wifi, Toast.LENGTH_LONG);
            return;
        }
        checkUpdateFromCloud();
    }

    private void checkAllUpdate() {
        checkUpdateFromLocalStorage(new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                checkNetworkForCloudUpdate();
            }
        });
    }

    private void checkUpdateFromCloud() {
        if (isOtaGuard()) {
            return;
        }
        setOtaGuard(true);
        showCheckingDialog();
        final FirmwareUpdateRequest updateRequest = OTAManager.cloudFirmwareCheckRequest(this);
        OTAManager.sharedInstance().submitRequest(this, updateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setOtaGuard(false);
                Firmware otaFirmware = updateRequest.getResultFirmware();
                if (e != null || otaFirmware == null || !updateRequest.isResultFirmwareValid()) {
                    printStackTrace(e);
                    showNoUpdateDialog();
                    return;
                }
                dismissCheckingDialog();
                showCloudUpdateDialog(otaFirmware);
            }
        });
    }

    public boolean isOtaGuard() {
        return otaGuard;
    }

    public void setOtaGuard(boolean otaGuard) {
        this.otaGuard = otaGuard;
    }

    private void checkUpdateFromLocalStorage(final BaseCallback callback) {
        final FirmwareLocalCheckLegalityRequest localRequest = OTAManager.localFirmwareCheckRequest(this);
        OTAManager.sharedInstance().submitRequest(this, localRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String targetPath = localRequest.getLegalityTargetPath();
                if (StringUtils.isNotBlank(targetPath)) {
                    dismissCheckingDialog();
                    showLocalUpdateDialog(targetPath);
                } else {
                    BaseCallback.invoke(callback, request, e);
                }
            }
        });
    }

    private void showCheckingDialog() {
        if (checkingDialog != null) {
            return;
        }
        checkingDialog = new OnyxAlertDialog();
        checkingDialog.setParams(new OnyxAlertDialog.Params()
                .setCanceledOnTouchOutside(false)
                .setEnableFunctionPanel(false)
                .setEnableNegativeButton(false)
                .setEnableTittle(false)
                .setPositiveButtonText(getString(R.string.ok))
                .setAlertMsgString(NEW_LINE + getString(R.string.checking_update) + NEW_LINE));
        checkingDialog.show(getFragmentManager(), "OTA_Checking");
    }

    private void showNoUpdateDialog() {
        if (checkingDialog == null) {
            return;
        }
        checkingDialog.setAlertMsg(NEW_LINE + getString(R.string.firmware_is_latest) + NEW_LINE);
        checkingDialog.setEnableFunctionPanel(true);
        checkingDialog.setPositiveButton(true, getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissCheckingDialog();
            }
        });
    }

    private void dismissCheckingDialog() {
        if (checkingDialog != null) {
            checkingDialog.dismiss();
            checkingDialog = null;
        }
    }

    private void showLocalUpdateDialog(final String path) {
        showUpdateDialog(getString(R.string.firmware_update_detected), getString(R.string.find_package_from_local),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OTAManager.sharedInstance().startFirmwareUpdate(FirmwareOTAActivity.this, path);
                    }
                });
    }

    private void showCloudUpdateDialog(final Firmware otaFirmware) {
        String messageString = otaFirmware.getChangeLog();
        if (StringUtils.isNullOrEmpty(messageString)) {
            messageString = otaFirmware.getFingerprint();
        }
        showUpdateDialog(getString(R.string.firmware_update_detected), messageString, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOTAFirmwareDownload(otaFirmware);
            }
        });
    }

    private void showUpdateDialog(String title, String msg, final View.OnClickListener clickListener) {
        final OnyxAlertDialog dlgOta = new OnyxAlertDialog();
        dlgOta.setParams(new OnyxAlertDialog.Params()
                .setTittleString(title)
                .setAlertMsgString(msg)
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlgOta.dismiss();
                        if (clickListener != null) {
                            clickListener.onClick(v);
                        }
                    }
                }));
        dlgOta.show(getFragmentManager(), "OTADialog");
    }

    private void startOTAFirmwareDownload(final Firmware otaFirmware) {
        if (!StringUtils.isUrl(otaFirmware.getUrl())) {
            ToastUtils.showToast(this, R.string.url_error);
            return;
        }
        final String filePath = OTAManager.CLOUD_PATH_SDCARD;
        final CloudFileDownloadRequest downloadRequest = new CloudFileDownloadRequest(otaFirmware.getUrl(), filePath, filePath) {
            @Override
            public void execute(CloudManager parent) throws Exception {
                //checksum
                File file = new File(filePath);
                String md5 = FileUtils.computeFullMD5Checksum(file);
                setMd5Valid(StringUtils.isNotBlank(md5) && md5.equals(otaFirmware.md5));
                if (!isMd5Valid()) {
                    file.delete();
                }
            }
        };
        final BaseDownloadTask task = OnyxDownloadManager.getInstance().download(downloadRequest, new BaseCallback() {

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                setProgressDialogProgressMessage(request, String.valueOf((int) info.progress) + "%");
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(request);
                if (e != null) {
                    printStackTrace(e);
                    showToast(R.string.download_interrupted, Toast.LENGTH_SHORT);
                    return;
                }
                if (!downloadRequest.isMd5Valid()) {
                    showToast(R.string.md5_verify_fail, Toast.LENGTH_SHORT);
                    return;
                }
                OTAManager.sharedInstance().startFirmwareUpdate(FirmwareOTAActivity.this, filePath);
            }
        });
        showProgressDialog(downloadRequest, R.string.downloading, new DialogProgressHolder.DialogCancelListener() {
            @Override
            public void onCancel() {
                task.pause();
            }
        });
        task.setForceReDownload(true);
        OnyxDownloadManager.getInstance().startDownload(task);
    }

    private void printStackTrace(Throwable e) {
        if (e != null) {
            e.printStackTrace();
        }
    }

    public static class OTASettingPreferenceFragment extends PreferenceFragmentCompat {
        Preference modelSpecPreference, firmwareSpecPreference, androidVersionSpecPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.ota_setting);
            initView();
        }

        private void initView() {
            modelSpecPreference = findPreference(getString(R.string.model_spec_key));
            firmwareSpecPreference = findPreference(getString(R.string.firmware_spec_key));
            androidVersionSpecPreference = findPreference(getString(R.string.android_version_spec_key));
        }

        @Override
        public void onResume() {
            super.onResume();
            updateData();
        }

        private void updateData() {
            modelSpecPreference.setSummary(Build.DEVICE);
            firmwareSpecPreference.setSummary(Build.ID);
            androidVersionSpecPreference.setSummary(Build.VERSION.RELEASE);
        }
    }
}
