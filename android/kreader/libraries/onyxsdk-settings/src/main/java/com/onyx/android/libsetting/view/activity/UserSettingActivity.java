package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityUserSettingBinding;

public class UserSettingActivity extends OnyxAppCompatActivity {
    ActivityUserSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.user_preference,
                new GeneralPreferenceFragment()).commit();
    }

    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {
        private CheckBoxPreference mOpenLastReadCheckBox = null;
        private CheckBoxPreference mAutoScanAfterSDCardMountCheckBox = null;
        private CheckBoxPreference mAutoScanAfterPCDisconnectCheckBox = null;
        private CheckBoxPreference mScanOnStorageCheckBox = null;
        private CheckBoxPreference mScanOnSDCardCheckBox = null;
        private CheckBoxPreference mUseTittleInLibraryCheckBox = null;
        private PreferenceCategory mPageHomePreferenceCategory;
        private ListPreference mPageHomePreferenceList;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.user_setting);
            mOpenLastReadCheckBox = (CheckBoxPreference) findPreference(this.getResources().getString(R.string.open_last_document));
            mAutoScanAfterSDCardMountCheckBox = (CheckBoxPreference) findPreference(
                    this.getResources().getString(R.string.library_sd_scan_option_key));
            mAutoScanAfterPCDisconnectCheckBox = (CheckBoxPreference) findPreference(
                    this.getResources().getString(R.string.library_pc_scan_option_key));
            mScanOnStorageCheckBox = (CheckBoxPreference) findPreference(this.getResources().getString(R.string.scan_on_storage));
            mScanOnSDCardCheckBox = (CheckBoxPreference) findPreference(this.getResources().getString(R.string.scan_on_SDCard));
            mPageHomePreferenceCategory = (PreferenceCategory) findPreference(
                    this.getResources().getString(R.string.homepage_preference_category_key));
            mPageHomePreferenceList = (ListPreference) findPreference(
                    this.getResources().getString(R.string.homepage_preference_key));
            mUseTittleInLibraryCheckBox = (CheckBoxPreference) findPreference(getString(R.string.library_display_preference_key));
        }

//        private void initFunctions() {
//            mScanOnStorageCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    saveScanLimit((Boolean) newValue, mScanOnSDCardCheckBox.isChecked());
//                    return true;
//                }
//            });
//            mScanOnSDCardCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    saveScanLimit(mScanOnStorageCheckBox.isChecked(), (Boolean) newValue);
//                    return true;
//                }
//            });
//            mOpenLastReadCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    saveOpenLastDocumentSetting((Boolean) newValue);
//                    return true;
//                }
//            });
//            mAutoScanAfterSDCardMountCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    if ((Boolean) newValue) {
//                        mAutoScanAfterSDCardMountCheckBox.setSummary(R.string.enable);
//                    } else {
//                        mAutoScanAfterSDCardMountCheckBox.setSummary(R.string.disable);
//                    }
//                    return true;
//                }
//            });
//            mAutoScanAfterPCDisconnectCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    if ((Boolean) newValue) {
//                        mAutoScanAfterPCDisconnectCheckBox.setSummary(R.string.enable);
//                    } else {
//                        mAutoScanAfterPCDisconnectCheckBox.setSummary(R.string.disable);
//                    }
//                    return true;
//                }
//            });
//            mUseTittleInLibraryCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    if ((Boolean) newValue) {
//                        mUseTittleInLibraryCheckBox.setSummary(R.string.enable);
//                    } else {
//                        mUseTittleInLibraryCheckBox.setSummary(R.string.disable);
//                    }
//                    return true;
//                }
//            });
//        }
//
//        private void initCurrentStatus() {
//            boolean isChecked = OnyxSysCenter.getOpenLastReadDocument(getActivity());
//            if (isChecked) {
//                mOpenLastReadCheckBox.setChecked(true);
//            } else {
//                mOpenLastReadCheckBox.setChecked(false);
//            }
//            if (ContentBrowserPreference.getBooleanValue(getActivity(),
//                    R.string.library_pc_scan_option_key, true)) {
//                mAutoScanAfterPCDisconnectCheckBox.setSummary(R.string.enable);
//            } else {
//                mAutoScanAfterPCDisconnectCheckBox.setSummary(R.string.disable);
//            }
//            if (ContentBrowserPreference.getBooleanValue(getActivity(),
//                    R.string.library_sd_scan_option_key, true)) {
//                mAutoScanAfterSDCardMountCheckBox.setSummary(R.string.enable);
//            } else {
//                mAutoScanAfterSDCardMountCheckBox.setSummary(R.string.disable);
//            }
//            if (ContentBrowserUtils.isShowBookTittleInLibrary(getActivity())) {
//                mUseTittleInLibraryCheckBox.setSummary(R.string.enable);
//            } else {
//                mUseTittleInLibraryCheckBox.setSummary(R.string.disable);
//            }
//            ArrayList<String> dirs = OnyxSysCenter.getBookScanInternalDirectories(getActivity());
//            if (dirs != null && !dirs.contains(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath())) {
//                mScanOnStorageCheckBox.setChecked(true);
//            } else {
//                mScanOnStorageCheckBox.setChecked(false);
//            }
//            dirs = OnyxSysCenter.getBookScanExtSDDirectories(getActivity());
//            if (dirs != null && !dirs.contains(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath())) {
//                mScanOnSDCardCheckBox.setChecked(true);
//            } else {
//                mScanOnSDCardCheckBox.setChecked(false);
//            }
//            mPageHomePreferenceList.setDefaultValue(String.valueOf(ContentBrowserOptions.getRecentlyOption()));
//            if (mPageHomePreferenceCategory != null) {
//                mPageHomePreferenceCategory.removePreference(mPageHomePreferenceList);
//                mPageHomePreferenceCategory.addPreference(mPageHomePreferenceList);
//            }
//        }
//
//        @Override
//        public void onStop() {
//            saveOpenLastDocumentSetting(mOpenLastReadCheckBox.isChecked());
//            saveScanLimit(mScanOnStorageCheckBox.isChecked(), mScanOnSDCardCheckBox.isChecked());
//            super.onStop();
//        }
//
//        private void saveOpenLastDocumentSetting(boolean status) {
//            OnyxSysCenter.setOpenLastReadDocument(getActivity(), status);
//        }
//
//        private void saveScanLimit(boolean storageStatus, boolean sdcardStatus) {
//            final List<String> list = DeviceConfig.sharedInstance(getActivity()).getBookDirectories();
//            List<String> result = new ArrayList<String>();
//            ArrayList<String> dirs = new ArrayList<String>();
//            if (storageStatus) {
//                addDirectories(dirs, EnvironmentUtil.getExternalStorageDirectory(), list);
//                OnyxSysCenter.setBookScanInternalDirectories(getActivity(), dirs);
//            } else {
//                dirs.add(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
//                OnyxSysCenter.setBookScanInternalDirectories(getActivity(), dirs);
//            }
//            result.addAll(dirs);
//            dirs.clear();
//            if (sdcardStatus) {
//                addDirectories(dirs, EnvironmentUtil.getRemovableSDCardDirectory(), list);
//                OnyxSysCenter.setBookScanExtSDDirectories(getActivity(), dirs);
//            } else {
//                dirs.add(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
//                OnyxSysCenter.setBookScanExtSDDirectories(getActivity(), dirs);
//            }
//
//            result.addAll(dirs);
//            ContentBrowser.setTargetLibraryScanFolder(result);
//        }
//
//        private void addDirectories(final List<String> target, final File parent, final List<String> children) {
//            if (children == null) {
//                target.add(parent.getAbsolutePath());
//                return;
//            }
//            for (String child : children) {
//                final String s = new File(parent, child).getAbsolutePath();
//                target.add(s);
//            }
//        }
    }

}
