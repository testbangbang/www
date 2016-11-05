package com.onyx.android.note.activity.onyx;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.onyx.android.note.R;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

public class SettingActivity extends OnyxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        getSupportFragmentManager().beginTransaction().replace(R.id.preference_content,
                new SettingPreferenceFragment()).commit();
    }

    private void initViews() {
        setContentView(R.layout.activity_setting);
        initSupportActionBarWithCustomBackFunction();
    }

    public static class SettingPreferenceFragment extends PreferenceFragmentCompat {
        private Preference mVersionCodePreference = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            setHasOptionsMenu(false);
            mVersionCodePreference = findPreference(getString(R.string.setting_version_key));
            updateVersionCode();
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

        }

        private void updateVersionCode() {
            try {
                String versionCode = "";
                try {
                    versionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                    versionCode = versionCode + " (" +
                            getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode + ")";
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mVersionCodePreference.setSummary(versionCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
