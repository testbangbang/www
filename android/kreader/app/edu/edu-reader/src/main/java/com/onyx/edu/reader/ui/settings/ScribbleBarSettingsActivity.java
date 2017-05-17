package com.onyx.edu.reader.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.data.SingletonSharedPreference;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ScribbleBarSettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String[] lineWidth;
    private float delta = 0.1f;
    private float base = 0.1f;
    private static int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.scribblebar_settings);
        setContentView(R.layout.setting_main);
        RelativeLayout mBackFunctionLayout = (RelativeLayout) findViewById(R.id.back_function_layout);
        TextView settingTittle = (TextView) findViewById(R.id.settingTittle);
        settingTittle.setText(R.string.settings_scribble_bar_tittle);
        mBackFunctionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initShowReaderStatusBarPreference();
        SingletonSharedPreference.getPrefs().registerOnSharedPreferenceChangeListener(this);
        initLineWidthListPreferenceData();
    }

    private void initShowReaderStatusBarPreference(){
        if (!SingletonSharedPreference.isReaderStatusBarEnabled(this)) {
            findPreference(getString(R.string.settings_scribble_bar_show_reader_status_bar_key)).setEnabled(false);
        }
    }

    private void initLineWidthListPreferenceData(){
        lineWidth = new String[limit];
        float value = base;
        for(int i = 0; i < limit; ++i) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.DOWN);
            lineWidth[i] = df.format(value);
            value += delta;
        }
        ListPreference lineWidthPreference = (ListPreference) findPreference(
                getString(R.string.settings_scribble_base_width_key));
        lineWidthPreference.setEntries(lineWidth);
        lineWidthPreference.setEntryValues(lineWidth);
        lineWidthPreference.setSummary(lineWidthPreference.getEntry());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }

    public void finish() {
        SingletonSharedPreference.getPrefs().unregisterOnSharedPreferenceChangeListener(this);
        super.finish();
    }

}