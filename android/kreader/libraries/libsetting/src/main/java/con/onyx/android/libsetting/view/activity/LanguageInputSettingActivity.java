package con.onyx.android.libsetting.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.ActivityUtil;

import java.util.List;

import con.onyx.android.libsetting.R;
import con.onyx.android.libsetting.SettingConfig;
import con.onyx.android.libsetting.databinding.ActivityLanguageInputSettingBinding;
import con.onyx.android.libsetting.util.CommonUtil;
import con.onyx.android.libsetting.util.InputMethodLanguageSettingUtil;

public class LanguageInputSettingActivity extends OnyxAppCompatActivity {
    ActivityLanguageInputSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_input_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.input_preference,
                new LanguageInputSettingActivity.InputMethodAndTTSPreferenceFragment()).commit();
        binding.currentLanguage.setText(InputMethodLanguageSettingUtil.getCurrentLanguage(this));
        binding.changeLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LanguageInputSettingActivity.this, LocaleSelectActivity.class));
            }
        });
    }

    public static class InputMethodAndTTSPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.lang_input_setting);
            List<InputMethodInfo> inputMethodInfoList = InputMethodLanguageSettingUtil.getCurrentEnableIMEList(getContext());
            PreferenceCategory imeCategory = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.ime_category_key));
            for (InputMethodInfo info : inputMethodInfoList) {
                CheckBoxPreference preference = new CheckBoxPreference(
                        CommonUtil.buildDynamicPreferenceNeededContextWrapper(getActivity()));
                preference.setTitle(info.loadLabel(getActivity().getPackageManager()));
                preference.setKey(info.getId());
                imeCategory.addPreference(preference);
            }
            Preference ttsPreference = findPreference(getString(R.string.tts_settings_key));
            ttsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getActivity(),
                            SettingConfig.sharedInstance(getContext()).getTTSSettingIntent());
                    return true;
                }
            });
        }

    }
}
