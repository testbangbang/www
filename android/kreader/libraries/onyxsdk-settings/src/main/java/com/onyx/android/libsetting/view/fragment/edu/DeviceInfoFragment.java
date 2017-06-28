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
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

public class DeviceInfoFragment extends PreferenceFragmentCompat {
    CheckBoxPreference adbPreference;
    Preference vcomPreference, screenBarCodePreference, digitizerFwPreference;
    SettingConfig config;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.device_info);
        config = SettingConfig.sharedInstance(getContext().getApplicationContext());
        initView();
    }

    private void initView() {
        adbPreference = (CheckBoxPreference) findPreference(getString(R.string.usb_debug_key));
        vcomPreference = findPreference(getString(R.string.vcom_key));
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
    }


    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        adbPreference.setChecked(ApplicationSettingUtil.isEnableADB(getContext()));
        vcomPreference.setSummary(EduDeviceInfoUtil.getVComInfo());
        screenBarCodePreference.setSummary(EduDeviceInfoUtil.getBarCode());
        digitizerFwPreference.setSummary(EduDeviceInfoUtil.getDigitizerFW());
    }

}
