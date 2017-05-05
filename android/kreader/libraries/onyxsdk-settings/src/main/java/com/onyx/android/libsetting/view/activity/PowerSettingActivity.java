package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.onyx.android.libsetting.util.DeviceFeatureUtil;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.ActivityUtil;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.data.PowerSettingTimeoutCategory;
import com.onyx.android.libsetting.databinding.ActivityPowerSettingBinding;
import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.util.PowerUtil;

public class PowerSettingActivity extends OnyxAppCompatActivity {
    ActivityPowerSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_power_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.power_preference,
                new PowerSettingPreferenceFragment()).commit();
    }

    public static class PowerSettingPreferenceFragment extends PreferenceFragmentCompat {
        ListPreference autoSleepListPreference, autoPowerOffListPreference, networkLatencyListPreference;
        CheckBoxPreference wakeupPreference;
        SettingConfig config;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.power_setting);
            config = SettingConfig.sharedInstance(getContext());
            initView();
            hidePreferenceByDeviceFeature();
        }

        private void initView() {
            wakeupPreference = (CheckBoxPreference) findPreference(getString(R.string.wakeup_front_light_key));
            autoPowerOffListPreference = (ListPreference) findPreference(getString(R.string.auto_power_off_timeout_key));
            autoSleepListPreference = (ListPreference) findPreference(getString(R.string.auto_sleep_timeout_key));
            networkLatencyListPreference = (ListPreference) findPreference(getString(R.string.auto_wifi_sleep_timeout_key));
            wakeupPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    PowerUtil.setWakeUpFrontLightEnabled(getContext(), (Boolean) newValue);
                    return true;
                }
            });
            autoSleepListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    PowerUtil.setCurrentTimeoutValue(getContext(),
                            PowerSettingTimeoutCategory.SCREEN_TIMEOUT, Integer.parseInt((String) newValue));
                    updateAutoSleepListSummary(Integer.parseInt((String) newValue));
                    return true;
                }
            });
            autoPowerOffListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    PowerUtil.setCurrentTimeoutValue(getContext(),
                            PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT, Integer.parseInt((String) newValue));
                    return true;
                }
            });
            networkLatencyListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    PowerUtil.setCurrentTimeoutValue(getContext(),
                            PowerSettingTimeoutCategory.WIFI_INACTIVITY_TIMEOUT, Integer.parseInt((String) newValue));
                    return true;
                }
            });
            findPreference(getString(R.string.power_usage_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getContext(), SettingConfig.sharedInstance(getContext()).getBatteryStatusIntent());
                    return true;
                }
            });
        }

        private void updateData() {
            wakeupPreference.setChecked(PowerUtil.isWakeUpFrontLightEnabled(getContext()));

            autoSleepListPreference.setValue(PowerUtil.getCurrentTimeoutValue(getContext(),
                    PowerSettingTimeoutCategory.SCREEN_TIMEOUT));
            autoSleepListPreference.setEntries(PowerUtil.getTimeoutEntries(getContext(),
                    PowerSettingTimeoutCategory.SCREEN_TIMEOUT));
            autoSleepListPreference.setEntryValues(PowerUtil.getTimeoutEntryValues(getContext(),
                    PowerSettingTimeoutCategory.SCREEN_TIMEOUT));
            updateAutoSleepListSummary(Integer.parseInt(autoSleepListPreference.getValue()));

            autoPowerOffListPreference.setValue(PowerUtil.getCurrentTimeoutValue(getContext(),
                    PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT));
            autoPowerOffListPreference.setEntries(PowerUtil.getTimeoutEntries(getContext(),
                    PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT));
            autoPowerOffListPreference.setEntryValues(PowerUtil.getTimeoutEntryValues(getContext(),
                    PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT));

            networkLatencyListPreference.setValue(PowerUtil.getCurrentTimeoutValue(getContext(),
                    PowerSettingTimeoutCategory.WIFI_INACTIVITY_TIMEOUT));
            networkLatencyListPreference.setEntries(PowerUtil.getTimeoutEntries(getContext(),
                    PowerSettingTimeoutCategory.WIFI_INACTIVITY_TIMEOUT));
            networkLatencyListPreference.setEntryValues(PowerUtil.getTimeoutEntryValues(getContext(),
                    PowerSettingTimeoutCategory.WIFI_INACTIVITY_TIMEOUT));
        }

        private void updateAutoSleepListSummary(int ms) {
            autoSleepListPreference.setSummary(ms == -1 ? getString(R.string.never_sleep) :
                    getString(R.string.sleep_summary, CommonUtil.msToTimeStringWithUnit(getContext(), ms)));
        }

        private void hidePreferenceByDeviceFeature() {
            if (!DeviceFeatureUtil.hasWifi(getContext()) ||
                    !SettingConfig.sharedInstance(getContext()).isEnableNetworkLatencyConfig()) {
                networkLatencyListPreference.setVisible(false);
            }
            if (!DeviceFeatureUtil.hasFrontLight(getContext())) {
                wakeupPreference.setVisible(false);
            }

            if (!DeviceFeatureUtil.hasNaturalLight(getContext())) {
                wakeupPreference.setVisible(false);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            updateData();
        }
    }
}
