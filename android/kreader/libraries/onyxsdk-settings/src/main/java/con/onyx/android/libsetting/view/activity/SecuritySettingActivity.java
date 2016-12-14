package con.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.ActivityUtil;

import con.onyx.android.libsetting.R;
import con.onyx.android.libsetting.SettingConfig;
import con.onyx.android.libsetting.databinding.ActivitySecuritySettingBinding;

public class SecuritySettingActivity extends OnyxAppCompatActivity {
    ActivitySecuritySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_security_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.security_preference,
                new SecurityPreferenceFragment()).commit();
    }

    public static class SecurityPreferenceFragment extends PreferenceFragmentCompat {
        Preference dataBackupPreference, factoryResetPreference;
        SettingConfig config;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.security_setting);
            config = SettingConfig.sharedInstance(getContext());
            initView();
        }

        private void initView() {
            factoryResetPreference = findPreference(getString(R.string.factory_reset_key));
            factoryResetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getContext(), config.getFactoryResetIntent());
                    return true;
                }
            });
        }
    }
}
