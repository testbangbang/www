package con.onyx.android.libsetting.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.ActivityUtil;

import java.util.List;

import con.onyx.android.libsetting.R;
import con.onyx.android.libsetting.data.wifi.AccessPoint;
import con.onyx.android.libsetting.databinding.ActivityNetworkSettingBinding;
import con.onyx.android.libsetting.manager.WifiAdmin;
import con.onyx.android.libsetting.view.OnyxCustomSwitchPreference;

public class NetworkSettingActivity extends OnyxAppCompatActivity {
    ActivityNetworkSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_network_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.network_setting,
                new NetworkSettingPreferenceFragment()).commit();
    }

    public static class NetworkSettingPreferenceFragment extends PreferenceFragmentCompat {
        OnyxCustomSwitchPreference wifiSwitchPreference;
        WifiAdmin wifiAdmin;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.network_setting);
            initView();
            wifiAdmin = new WifiAdmin(getContext());
            wifiAdmin.setCallback(new WifiAdmin.Callback() {
                @Override
                public void onWifiStateChange(boolean isWifiEnable) {
                    wifiSwitchPreference.setSwitchChecked(isWifiEnable);
                }

                @Override
                public void onScanResultReady(List<AccessPoint> scanResult) {

                }

                @Override
                public void onSupplicantStateChanged(NetworkInfo.DetailedState state) {

                }

                @Override
                public void onNetworkConnectionChange(NetworkInfo.DetailedState state) {

                }
            });
        }

        private void initView() {
            wifiSwitchPreference = (OnyxCustomSwitchPreference) findPreference(getString(R.string.wifi_setting_key));
            wifiSwitchPreference.setCallback(new OnyxCustomSwitchPreference.Callback() {
                @Override
                public void onSwitchClicked() {
                    wifiAdmin.toggleWifi();
                }

                @Override
                public void onSwitchReady() {
                    wifiSwitchPreference.setSwitchChecked(wifiAdmin.isWifiEnabled());
                }
            });

            wifiSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getContext(), new Intent(getContext(), WifiSettingActivity.class));
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            wifiAdmin.registerReceiver();
        }

        @Override
        public void onPause() {
            super.onPause();
            wifiAdmin.unregisterReceiver();
        }
    }
}
