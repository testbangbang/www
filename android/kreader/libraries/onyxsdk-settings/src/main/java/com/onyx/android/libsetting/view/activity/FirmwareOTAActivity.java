package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityFirmwareOtaBinding;
import com.onyx.android.libsetting.util.DeviceFeatureUtil;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

public class FirmwareOTAActivity extends OnyxAppCompatActivity {
    ActivityFirmwareOtaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
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


    private void onCheckOTAFromCloud() {

    }

    private void onCheckOTAFromLocal() {

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
    }
}
