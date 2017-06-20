package com.onyx.android.libsetting.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.databinding.ActivityLanguageSettingBinding;
import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.util.InputMethodLanguageSettingUtil;
import com.onyx.android.libsetting.view.OnyxCustomIMEPreference;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

public class LanguageInputSettingActivity extends OnyxAppCompatActivity {
    ActivityLanguageSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.input_preference,
                new ImeTtsSettingPreferenceFragment()).commit();
        binding.currentLanguage.setText(InputMethodLanguageSettingUtil.getCurrentLanguage(this));
        binding.changeLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LanguageInputSettingActivity.this, LocaleSelectActivity.class));
            }
        });
    }

    public static class ImeTtsSettingPreferenceFragment extends PreferenceFragmentCompat {
        ListPreference defaultImePreference;
        PreferenceCategory imeCategory;
        List<OnyxCustomIMEPreference> imePreferenceList = new ArrayList<>();

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.lang_input_setting);
            initView();
        }

        private void initView() {
            defaultImePreference = (ListPreference) findPreference(getString(R.string.default_ime_key));
            imeCategory = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.ime_category_key));
            Preference ttsPreference = findPreference(getString(R.string.tts_settings_key));
            ttsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getActivity(),
                            SettingConfig.sharedInstance(getContext()).getTTSSettingIntent());
                    return true;
                }
            });
            defaultImePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    InputMethodLanguageSettingUtil.setSpecificIMEDefault(getContext(), (String) value);
                    updateDefaultIMESummary();
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            onPostResume();
        }

        private void onPostResume() {
            updateDefaultIMEPreference();
            updateInstalledIMEPreference();
        }

        private void updateDefaultIMESummary() {
            defaultImePreference.setSummary(InputMethodLanguageSettingUtil.getDefaultIMEName(getContext()));
        }

        private void updateDefaultIMEPreference() {
            List<InputMethodInfo> inputMethodInfoList = InputMethodLanguageSettingUtil.getCurrentEnableIMEList(getContext());
            CharSequence[] entries = new CharSequence[InputMethodLanguageSettingUtil.getCurrentEnableIMEList(getContext()).size()];
            CharSequence[] entriesValue = new CharSequence[InputMethodLanguageSettingUtil.getCurrentEnableIMEList(getContext()).size()];
            for (int i = 0; i < inputMethodInfoList.size(); i++) {
                entries[i] = inputMethodInfoList.get(i).loadLabel(getActivity().getPackageManager());
                entriesValue[i] = inputMethodInfoList.get(i).getId();
            }
            defaultImePreference.setEntries(entries);
            defaultImePreference.setEntryValues(entriesValue);
            defaultImePreference.setValue(InputMethodLanguageSettingUtil.getDefaultIMEID(getContext()));
            updateDefaultIMESummary();
        }

        private void updateInstalledIMEPreference() {
            List<InputMethodInfo> inputMethodInfoList = InputMethodLanguageSettingUtil.getInstalledIMEList(getContext());
            for (OnyxCustomIMEPreference imePreference : imePreferenceList) {
                imeCategory.removePreference(imePreference);
            }
            imePreferenceList.clear();
            for (InputMethodInfo info : inputMethodInfoList) {
                final OnyxCustomIMEPreference preference = new OnyxCustomIMEPreference(
                        CommonUtil.buildDynamicPreferenceNeededContextWrapper(getActivity()));
                preference.setTitle(info.loadLabel(getActivity().getPackageManager()));
                preference.setKey(info.getId());
                preference.setImeInfo(info);
                preference.setCallback(new OnyxCustomIMEPreference.Callback() {
                    @Override
                    public void onCheckBoxReady() {
                        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.KITKAT)) {
                            preference.setIMECheckedEnabled(
                                    !InputMethodLanguageSettingUtil.isSystemPreservedIme(preference.getImeInfo()));
                        } else {
                            preference.setIMECheckedEnabled(!InputMethodLanguageSettingUtil.
                                    isSystemIme(preference.getImeInfo()));
                        }
                        preference.setIMEChecked(InputMethodLanguageSettingUtil.
                                isSpecificIMEEnabled(getContext(), preference.getKey()));
                    }
                });
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference pf) {
                        OnyxCustomIMEPreference imePf = (OnyxCustomIMEPreference) pf;
                        InputMethodLanguageSettingUtil.setSpecificIMEEnabled(getContext(), imePf.getKey(),
                                imePf.isIMEChecked());
                        updateDefaultIMEPreference();
                        return true;
                    }
                });
                imePreferenceList.add(preference);
                imeCategory.addPreference(preference);
            }
        }

    }
}
