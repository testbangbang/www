package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityFirmwareOtaBinding;
import com.onyx.android.libsetting.manager.OTAAdmin;
import com.onyx.android.libsetting.manager.SettingsPreferenceManager;
import com.onyx.android.libsetting.util.DeviceFeatureUtil;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.OTAFirmware;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.utils.StringUtils;

public class FirmwareOTAActivity extends OnyxAppCompatActivity {
    public static final String ACTION_OTA_DOWNLOAD = "com.action.ota.download";

    ActivityFirmwareOtaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        processIntent();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_firmware_ota);
        initSupportActionBarWithCustomBackFunction();
        binding.buttonCloudOta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckOTAFromCloud();
            }
        });
        binding.buttonLocalOta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckOTAFromLocal();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.ota_info_preference,
                new OTASettingPreferenceFragment()).commit();
        binding.buttonCloudOta.setVisibility(DeviceFeatureUtil.hasWifi(this) ? View.VISIBLE : View.GONE);
    }

    private void processIntent() {
        String action = getIntent().getAction();
        if (ACTION_OTA_DOWNLOAD.equals(action)) {
            onCheckOTAFromCloud();
        }
    }

    private void onCheckOTAFromCloud() {
        final OTAAdmin otaAdmin = OTAAdmin.sharedInstance();
        otaAdmin.checkCloudFirmware(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                OTAFirmware otaFirmware;
                if (e != null || (otaFirmware = otaAdmin.checkCloudOTAFirmware(FirmwareOTAActivity.this, request)) == null) {
                    printStackTrace(e);
                    showToast(R.string.no_update, Toast.LENGTH_SHORT);
                    return;
                }
                showCloudUpdateDialog(otaFirmware);
            }
        });
        showToast("正在检查网络更新...", Toast.LENGTH_SHORT);
    }

    private void onCheckOTAFromLocal() {
        OTAAdmin.sharedInstance().checkLocalFirmware(this,new OTAAdmin.FirmwareCheckCallback() {
            @Override
            public void preCheck() {

            }

            @Override
            public void stateChanged(int state, long finished, long total, long percentage) {

            }

            @Override
            public void onPostCheck(String targetPath, boolean success) {
                if (success) {
                    showLocalUpdateDialog(targetPath);
                } else {
                    showToast(R.string.no_update, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void showLocalUpdateDialog(final String path) {
        final OnyxAlertDialog dlgOtaLocal = new OnyxAlertDialog();
        dlgOtaLocal.setParams(new OnyxAlertDialog.Params().setAlertMsgString(getString(R.string.find_package_from_local))
                .setTittleString(getString(R.string.title_update))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OTAAdmin.sharedInstance().startFirmwareUpdate(FirmwareOTAActivity.this, path);
                        dlgOtaLocal.dismiss();
                    }
                }));
        dlgOtaLocal.show(getFragmentManager(), "OTADialog");
    }

    private void showCloudUpdateDialog(final OTAFirmware otaFirmware) {
        String messageString = otaFirmware.changeLogText;
        if (StringUtils.isNullOrEmpty(messageString)) {
            messageString = otaFirmware.fingerPrint;
        }
        final OnyxAlertDialog dlgOtaCloud = new OnyxAlertDialog();
        dlgOtaCloud.setParams(new OnyxAlertDialog.Params()
                .setTittleString(getString(R.string.title_update))
                .setAlertMsgString(messageString)
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlgOtaCloud.dismiss();
                        startOTAFirmwareDownload(otaFirmware);
                    }
                }));
        dlgOtaCloud.show(getFragmentManager(), "OTADialog");
    }

    private void startOTAFirmwareDownload(OTAFirmware otaFirmware) {
        String filePath = OTAAdmin.LOCAL_PATH_SDCARD;
        CloudFileDownloadRequest downloadRequest = new CloudFileDownloadRequest(otaFirmware.url, filePath, filePath) {
            @Override
            public void execute(CloudManager parent) throws Exception {
                //checksum
            }
        };
        BaseDownloadTask task = OnyxDownloadManager.getInstance().download(downloadRequest, new BaseCallback() {

            @Override
            public void start(BaseRequest request) {
                showProgressDialog(request, null);
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                setProgressDialogProgressMessage(request, String.valueOf((int) info.progress) + "%");
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(request);
                if (e != null) {
                    printStackTrace(e);
                    showToast(R.string.no_update, Toast.LENGTH_SHORT);
                    return;
                }
                onCheckOTAFromLocal();
            }
        });
        OnyxDownloadManager.getInstance().startDownload(task);
    }

    private void printStackTrace(Throwable e) {
        if (e != null) {
            e.printStackTrace();
        }
    }

    public static class OTASettingPreferenceFragment extends PreferenceFragmentCompat {
        CheckBoxPreference otaAutoCheckPreference;
        Preference modelSpecPreference, firmwareSpecPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.ota_setting);
            initView();
        }

        private void initView() {
            otaAutoCheckPreference = (CheckBoxPreference) findPreference(getString(R.string.ota_auto_check_key));
            modelSpecPreference = findPreference(getString(R.string.model_spec_key));
            firmwareSpecPreference = findPreference(getString(R.string.firmware_spec_key));
            otaAutoCheckPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    saveAutoFirmwareCheck((Boolean) newValue);
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            updateData();
        }

        private void updateData() {
            if (!DeviceFeatureUtil.hasWifi(getContext())) {
                otaAutoCheckPreference.setVisible(false);
            }
            modelSpecPreference.setSummary(Build.DEVICE);
            firmwareSpecPreference.setSummary(Build.DISPLAY);
        }

        private void saveAutoFirmwareCheck(boolean newValue){
            SettingsPreferenceManager.setFirmwareCheckWhenWifiConnected(getActivity(), newValue);
        }
    }
}
