package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.Gravity;
import android.view.View;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.databinding.ActivityApplicationSettingBinding;
import com.onyx.android.libsetting.util.ApplicationSettingUtil;
import com.onyx.android.libsetting.util.DeviceFeatureUtil;
import com.onyx.android.libsetting.util.KeyBindingSettingUtil;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.utils.ActivityUtil;

public class ApplicationSettingActivity extends OnyxAppCompatActivity {
    ActivityApplicationSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_application_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.application_setting_preference,
                new ApplicationSettingPreferenceFragment()).commit();
    }

    public static class ApplicationSettingPreferenceFragment extends PreferenceFragmentCompat {
        CheckBoxPreference adbPreference;
        CheckBoxPreference unknownResourcePreference;
        Preference applicationManagement, drmSetting, calibration;
        ListPreference keyBinding, longPressFeatureSetting;
        SettingConfig config;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.application_setting);
            config = SettingConfig.sharedInstance(getContext());
            initView();
        }

        private void initView() {
            adbPreference = (CheckBoxPreference) findPreference(getString(R.string.usb_debug_key));
            unknownResourcePreference = (CheckBoxPreference) findPreference(getString(R.string.unknown_resource_key));
            applicationManagement = findPreference(getString(R.string.application_management_key));
            drmSetting = findPreference(getString(R.string.drm_setting_key));
            calibration = findPreference(getString(R.string.calibration_key));
            applicationManagement.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getContext(), config.getApplicationManagementIntent());
                    return true;
                }
            });
            drmSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getContext(), config.getDRMSettingIntent());
                    return true;
                }
            });
            calibration.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getContext(), config.getCalibrationIntent());
                    return true;
                }
            });

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
            unknownResourcePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        final OnyxAlertDialog unknownResourceConfirmDlg = new OnyxAlertDialog();
                        unknownResourceConfirmDlg.setParams(new OnyxAlertDialog.Params()
                                .setCanceledOnTouchOutside(false)
                                .setEnableTittle(false)
                                .setAlertMsgString(getString(R.string.install_all_warning))
                                .setAlertMsgGravity(Gravity.START)
                                .setPositiveAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ApplicationSettingUtil.setNonMarketAppsAllowed(getContext(), true);
                                        unknownResourceConfirmDlg.dismiss();
                                    }
                                })
                                .setNegativeAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        unknownResourcePreference.setChecked(false);
                                        unknownResourceConfirmDlg.dismiss();
                                    }
                                }));
                        unknownResourceConfirmDlg.show(getActivity().getFragmentManager(), "Non-Market App Alert");
                    } else {
                        ApplicationSettingUtil.setNonMarketAppsAllowed(getContext(), false);
                    }
                    return true;
                }
            });
            initKeyBindSetting();
            if (SettingConfig.sharedInstance(getContext()).hideDRMSettings()){
                getPreferenceScreen().removePreference(drmSetting);
            }
        }

        private void initKeyBindSetting() {
            keyBinding = (ListPreference) findPreference(getString(R.string.key_binding_key));
            longPressFeatureSetting = (ListPreference) findPreference(getString(R.string.long_press_feature_key));
            // TODO: 2016/12/21 tp_model for what purpose?Should use a better check?
            if (!config.isEnableKeyBinding()) {
                keyBinding.setVisible(false);
                longPressFeatureSetting.setVisible(false);
                return;
            }
            keyBinding.setEntryValues(getContext().getResources().getStringArray(
                    DeviceFeatureUtil.hasTouch(getContext()) ?
                            R.array.long_short_click_key_map_mode_value :
                            R.array.long_short_click_key_map_mode_value_for_tp_model));
            keyBinding.setEntries(getContext().getResources().getStringArray(
                    DeviceFeatureUtil.hasTouch(getContext()) ?
                            R.array.long_short_click_key_map_summary :
                            R.array.long_short_click_key_map_summary_for_tp_model));
            longPressFeatureSetting.setVisible(!DeviceFeatureUtil.hasTouch(getContext()));
            keyBinding.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    KeyBindingSettingUtil.setKeyMapMode(getContext(), Integer.parseInt((String) o));
                    return true;
                }
            });
            longPressFeatureSetting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    KeyBindingSettingUtil.setDpadLongPressFeature(getContext(), Integer.parseInt((String) o));
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
            adbPreference.setChecked(ApplicationSettingUtil.isEnableADB(getContext()));
            unknownResourcePreference.setChecked(ApplicationSettingUtil.isNonMarketAppsAllowed(getContext()));
            keyBinding.setValue(KeyBindingSettingUtil.getKeyMapMode(getContext()));
            longPressFeatureSetting.setValue(KeyBindingSettingUtil.getDpadLongPressFeature(getContext()));
        }

    }
}
