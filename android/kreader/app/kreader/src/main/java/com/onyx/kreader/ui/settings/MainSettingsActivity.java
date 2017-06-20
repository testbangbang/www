package com.onyx.kreader.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.onyx.kreader.R;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.util.HashMap;

public class MainSettingsActivity extends PreferenceActivity {
    private static final String TAG = MainSettingsActivity.class.getSimpleName();
    public final HashMap<String,Intent> sPreferenceIntentHashMap = new HashMap<String, Intent>();
    private Intent mScribbleBarIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_settings);
        setContentView(R.layout.setting_main);
        Intent mSystemIntent=new Intent(MainSettingsActivity.this, SystemSettingsActivity.class);
        Intent mScreenIntent=new Intent(MainSettingsActivity.this, ScreenSettingsActivity.class);
        Intent mConfigIntent=new Intent(MainSettingsActivity.this, ConfigSettingsActivity.class);
        Intent mControlIntent=new Intent(MainSettingsActivity.this, ControlSettingsActivity.class);
        Intent mStatusBarIntent=new Intent(MainSettingsActivity.this, StatusBarSettingsActivity.class);
        mScribbleBarIntent=new Intent(MainSettingsActivity.this, ScribbleBarSettingsActivity.class);
        sPreferenceIntentHashMap.put(getString(R.string.settings_system_key), mSystemIntent);
        sPreferenceIntentHashMap.put(getString(R.string.settings_screen_key), mScreenIntent);
//        sPreferenceIntentHashMap.put(getString(R.string.setting_config_key), mConfigIntent);
        sPreferenceIntentHashMap.put(getString(R.string.settings_control_key), mControlIntent);
        sPreferenceIntentHashMap.put(getString(R.string.settings_status_bar_key), mStatusBarIntent);
        sPreferenceIntentHashMap.put(getString(R.string.settings_scribble_bar_key), mScribbleBarIntent);
        RelativeLayout mBackFunctionLayout = (RelativeLayout) findViewById(R.id.back_function_layout);
        mBackFunctionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (DeviceUtils.isRkDevice()) {
            Preference preference = getPreferenceScreen().findPreference(getString(R.string.settings_scribble_bar_key));
            if (preference != null) {
                getPreferenceScreen().removePreference(preference);
            }
        }else {
            sPreferenceIntentHashMap.put(getString(R.string.settings_scribble_bar_key), mScribbleBarIntent);
        }
        if (DeviceConfig.sharedInstance(this).isHideControlSettings()) {
            Preference preference = getPreferenceScreen().findPreference(getString(R.string.settings_control_key));
            if (preference != null) {
                getPreferenceScreen().removePreference(preference);
            }
        }
        updateVersionName();
    }

    private void updateVersionName() {
        try {
            findPreference(getString(R.string.settings_version_key)).setSummary(DeviceUtils.getApplicationFingerprint(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (key != null) {
            Intent mIntent=sPreferenceIntentHashMap.get(key);
            if (mIntent != null) {
                startActivity(mIntent);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

}
