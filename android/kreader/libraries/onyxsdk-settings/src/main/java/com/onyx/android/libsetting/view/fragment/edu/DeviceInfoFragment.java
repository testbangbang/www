package com.onyx.android.libsetting.view.fragment.edu;

import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.Gravity;
import android.view.View;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.util.ApplicationSettingUtil;
import com.onyx.android.libsetting.util.EduDeviceInfoUtil;
import com.onyx.android.libsetting.view.dialog.ScreenBarCodeDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.common.PanelInfo;
import com.onyx.android.sdk.data.request.common.WaveformUpdateRequest;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.DeviceInfoUtil;
import com.onyx.android.sdk.utils.NetworkUtil;

public class DeviceInfoFragment extends PreferenceFragmentCompat {
    CheckBoxPreference adbPreference;
    Preference vcomPreference, waveformPreference, screenBarCodePreference, digitizerFwPreference;
    SettingConfig config;

    DialogProgressHolder dialogHolder = new DialogProgressHolder();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.device_info);
        config = SettingConfig.sharedInstance(getContext().getApplicationContext());
        initView();
    }

    private void initView() {
        adbPreference = (CheckBoxPreference) findPreference(getString(R.string.usb_debug_key));
        vcomPreference = findPreference(getString(R.string.vcom_key));
        waveformPreference = findPreference(getString(R.string.waveform_key));
        screenBarCodePreference = findPreference(getString(R.string.barcode_key));
        digitizerFwPreference = findPreference(getString(R.string.digitizer_fw_key));

        // TODO: 2016/12/21 should simplify this method.
        adbPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    final OnyxAlertDialog adbConfirmDlg = new OnyxAlertDialog();
                    adbConfirmDlg.setParams(new OnyxAlertDialog.Params()
                            .setCanceledOnTouchOutside(false)
                            .setTittleString(getString(R.string.adb_warning_title))
                            .setAlertMsgString(getString(R.string.adb_warning_msg))
                            .setAlertMsgGravity(Gravity.START)
                            .setPositiveAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ApplicationSettingUtil.setADBEnabled(getContext(), true);
                                    adbConfirmDlg.dismiss();
                                }
                            })
                            .setNegativeAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adbPreference.setChecked(false);
                                    adbConfirmDlg.dismiss();
                                }
                            }));
                    adbConfirmDlg.show(getActivity().getFragmentManager(), "ADB Alert");
                } else {
                    ApplicationSettingUtil.setADBEnabled(getContext(), false);
                }
                return true;
            }
        });

        screenBarCodePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ScreenBarCodeDialog screenBarCodeDialog = new ScreenBarCodeDialog();
                screenBarCodeDialog.show(getActivity().getFragmentManager());
                return false;
            }
        });
        waveformPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                checkWaveformUpdate();
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        adbPreference.setChecked(ApplicationSettingUtil.isEnableADB(getContext()));
        vcomPreference.setSummary(EduDeviceInfoUtil.getVComInfo());
        waveformPreference.setSummary(DeviceInfoUtil.getPanelWaveFormVersion());
        screenBarCodePreference.setSummary(EduDeviceInfoUtil.getBarCode());
        digitizerFwPreference.setSummary(EduDeviceInfoUtil.getDigitizerFW());
    }

    private void checkWaveformUpdate() {
        if (NetworkUtil.enableWifiOpenAndDetect(getContext())) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.opening_wifi);
            return;
        }
        startFetchWaveform();
    }

    private void startFetchWaveform() {
        final String key = Constant.OTA_SERVER_API;
        if (dialogHolder.getProgressDialogFromRequest(key) != null) {
            return;
        }
        final WaveformUpdateRequest fetchUpdateRequest = new WaveformUpdateRequest(Constant.OTA_SERVER_API,
                PanelInfo.create(getContext()), OTAManager.WAVEFORM_PATH, OTAManager.WAVEFORM_MD5_PATH);
        OTAManager.sharedInstance().getCloudStore().submitRequest(getContext().getApplicationContext(),
                fetchUpdateRequest, new BaseCallback() {

                    @Override
                    public void progress(BaseRequest request, ProgressInfo info) {
                        dialogHolder.setMessage(key, String.valueOf((int) info.progress) + "%");
                    }

                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dialogHolder.dismissProgressDialog(key);
                        int msgId = R.string.waveform_update_success;
                        if (e != null || !fetchUpdateRequest.isSuccessful()) {
                            msgId = R.string.waveform_no_update;
                        }
                        if (fetchUpdateRequest.isUpdated()) {
                            msgId = R.string.waveform_is_latest;
                        }
                        ToastUtils.showToast(getContext().getApplicationContext(), msgId);
                    }
                });
        dialogHolder.showProgressDialog(getContext(), key, R.string.loading, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialogHolder.dismissAllProgressDialog();
    }
}
