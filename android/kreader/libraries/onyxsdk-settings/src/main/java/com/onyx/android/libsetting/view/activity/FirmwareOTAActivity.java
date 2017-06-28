package com.onyx.android.libsetting.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityFirmwareOtaBinding;
import com.onyx.android.libsetting.util.DeviceFeatureUtil;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareLocalCheckLegalityRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogInformation;
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
    private boolean otaCheckingGuard = false;

    private DialogInformation informationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDeviceReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAllUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiver.enable(this, false);
    }

    private void initDeviceReceiver() {
        receiver.setWifiStateListener(new DeviceReceiver.WifiStateListener() {
            @Override
            public void onWifiStateChanged(Intent intent) {
            }

            @Override
            public void onWifiConnected(Intent intent) {
                checkNetworkForCloudUpdate();
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

    private void cleanup() {
        dismissInformationDialog();
        setOtaCheckingGuard(false);
    }

    private void checkNetworkForCloudUpdate() {
        if (!DeviceFeatureUtil.hasWifi(this)) {
            cleanup();
            return;
        }
        if (!NetworkHelper.isWifiConnected(FirmwareOTAActivity.this)) {
            receiver.enable(this, true);
            Device.currentDevice().enableWifiDetect(FirmwareOTAActivity.this);
            NetworkHelper.enableWifi(FirmwareOTAActivity.this, true);
            showMessage(getString(R.string.opening_wifi));
            return;
        }
        checkUpdateFromCloud();
    }

    private void checkAllUpdate() {
        if (isOtaCheckingGuard()) {
            return;
        }
        setOtaCheckingGuard(true);
        showInformationDialog(getString(R.string.checking_update));
        checkUpdateFromLocalStorage(new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                checkNetworkForCloudUpdate();
            }
        });
    }

    private void checkUpdateFromCloud() {
        showMessage(getString(R.string.checking_update));
        CloudRequestChain requestChain = new CloudRequestChain();
        requestChain.setAbortException(false);
        addIndexLookupRequest(this, requestChain);
        addCloudFirmwareCheckRequest(this, requestChain);
        requestChain.execute(this, OTAManager.sharedInstance().getCloudStore().getCloudManager());
    }

    private void addCloudFirmwareCheckRequest(final Context context, final CloudRequestChain requestChain) {
        final FirmwareUpdateRequest updateRequest = OTAManager.cloudFirmwareCheckRequest(context);
        requestChain.addRequest(updateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !updateRequest.isResultFirmwareValid()) {
                    printStackTrace(e);
                    showNoUpdateDialog();
                    return;
                }
                cleanup();
                Firmware otaFirmware = updateRequest.getResultFirmware();
                showCloudUpdateDialog(otaFirmware);
            }
        });
    }

    private void addIndexLookupRequest(final Context context, final CloudRequestChain requestChain) {
        final CloudIndexServiceRequest indexServiceRequest = new CloudIndexServiceRequest(
                Constant.CLOUD_MAIN_INDEX_SERVER_API,
                OTAManager.sharedInstance().createIndexService(context));
        indexServiceRequest.setLocalLoadRetryCount(3);
        requestChain.addRequest(indexServiceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !IndexService.hasValidServer(indexServiceRequest.getResultIndexService())) {
                    Log.w("FirmwareOTA", "indexService error, ready to use backup service");
                    OTAManager.sharedInstance().useLocalServerCloudConf();
                }
            }
        });
    }

    public boolean isOtaCheckingGuard() {
        return otaCheckingGuard;
    }

    public void setOtaCheckingGuard(boolean otaCheckingGuard) {
        this.otaCheckingGuard = otaCheckingGuard;
    }

    private void checkUpdateFromLocalStorage(final BaseCallback callback) {
        final FirmwareLocalCheckLegalityRequest localRequest = OTAManager.localFirmwareCheckRequest(this);
        OTAManager.sharedInstance().submitRequest(this, localRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String targetPath = localRequest.getLegalityTargetPath();
                if (StringUtils.isNotBlank(targetPath)) {
                    cleanup();
                    showLocalUpdateDialog(targetPath);
                } else {
                    BaseCallback.invoke(callback, request, e);
                }
            }
        });
    }

    private void showInformationDialog(final String message) {
        if (informationDialog != null) {
            return;
        }
        informationDialog = new DialogInformation(this);
        informationDialog.setParams(new DialogInformation.Params()
                .setEnableTittle(false)
                .setAlertMsgString(NEW_LINE + message + NEW_LINE)
                .setEnablePositiveButton(false)
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cleanup();
                    }
                }));
        informationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cleanup();
            }
        });
        informationDialog.show();
    }

    private void showMessage(final String message) {
        if (informationDialog == null) {
            showInformationDialog(message);
            return;
        }
        informationDialog.setAlertMsg(message);
    }

    private void showNoUpdateDialog() {
        showMessage(getString(R.string.firmware_is_latest));
    }

    private void dismissInformationDialog() {
        if (informationDialog != null) {
            informationDialog.dismiss();
            informationDialog = null;
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
        final DialogInformation dlgOta = new DialogInformation(this);
        dlgOta.setParams(new DialogInformation.Params()
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
        dlgOta.show();
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
