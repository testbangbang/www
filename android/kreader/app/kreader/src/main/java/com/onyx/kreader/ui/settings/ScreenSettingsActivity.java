package com.onyx.kreader.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.kreader.R;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.device.DeviceConfig;

public class ScreenSettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.screen_settings);
        setContentView(R.layout.setting_main);
        RelativeLayout mBackFunctionLayout = (RelativeLayout) findViewById(R.id.back_function_layout);
        TextView settingTittle=(TextView)findViewById(R.id.settingTittle);
        settingTittle.setText(R.string.settings_screen_tittle);
        mBackFunctionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SingletonSharedPreference.getPrefs().registerOnSharedPreferenceChangeListener(this);
        initAnnotationHighlightStylePreferenceData();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }

    private void initAnnotationHighlightStylePreferenceData() {
        ListPreference preference = (ListPreference) findPreference(
                getString(R.string.settings_annotation_highlight_style_key));
        Pair<SingletonSharedPreference.AnnotationHighlightStyle, Integer>[] pairs = new Pair[] {
                new Pair(SingletonSharedPreference.AnnotationHighlightStyle.Highlight, R.string.settings_highlight_style_highlight),
                new Pair(SingletonSharedPreference.AnnotationHighlightStyle.Underline, R.string.settings_highlight_style_underline),
        };
        String[] entries = new String[pairs.length];
        String[] values = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            Pair<SingletonSharedPreference.AnnotationHighlightStyle, Integer> pair = pairs[i];
            entries[i] = getBaseContext().getString(pair.second);
            values[i] = pair.first.toString();
        }
        preference.setEntries(entries);
        preference.setEntryValues(values);
        preference.setSummary(preference.getEntry());
        if (preference.getValue() == null) {
            preference.setValue(DeviceConfig.sharedInstance(getBaseContext()).defaultAnnotationHighlightStyle().toString());
        }

        Preference regal = findPreference(getString(R.string.settings_regal_mode_key));
        if (!(ReaderDeviceManager.supportRegal(getBaseContext())) && regal != null) {
            getPreferenceScreen().removePreference(regal);
        }
    }


    @Override
    public void finish() {
        SingletonSharedPreference.getPrefs().unregisterOnSharedPreferenceChangeListener(this);
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}
