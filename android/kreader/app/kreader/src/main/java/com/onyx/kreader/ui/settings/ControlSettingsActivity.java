package com.onyx.kreader.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.CustomBindKeyBean;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

public class ControlSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.control_settings);
        setContentView(R.layout.setting_main);
        RelativeLayout mBackFunctionLayout = (RelativeLayout) findViewById(R.id.back_function_layout);
        TextView settingTittle = (TextView) findViewById(R.id.settingTittle);
        settingTittle.setText(R.string.settings_control_tittle);
        mBackFunctionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals(getResources().getString(R.string.settings_key_binding_reset_default_key))) {
            new AlertDialog.Builder(ControlSettingsActivity.this)
                    .setMessage(getResources().getString(R.string.settings_key_binding_reset_default) + "?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cleanUpAllCustomBinding(preferenceScreen);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
            return true;
        }
        DialogBindHotKey dlg = new DialogBindHotKey(this, preference.getTitle().toString(),
                preference.getKey(), SingletonSharedPreference.getIntByStringResource(preference.getKey(), 0));
        dlg.setOnFilterListener(new DialogBindHotKey.OnUserSetKeyListener() {
            @Override
            public void onSet(String keyCodeString, String function, String args) {
                CustomBindKeyBean bean = new CustomBindKeyBean();
                bean.setAction(function);
                bean.setArgs(args);
                SingletonSharedPreference.setStringValue(keyCodeString, JSON.toJSONString(bean));
                SingletonSharedPreference.setIntValue(function, KeyEvent.keyCodeFromString(keyCodeString));
            }
        });
        dlg.show();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void cleanUpAllCustomBinding(PreferenceScreen screen) {
        for (int i = 0; i < screen.getPreferenceCount(); i++) {
            int keyCode = SingletonSharedPreference.getIntByStringResource(screen.getPreference(i).getKey(), 0);
            if (keyCode != 0) {
                SingletonSharedPreference.removeValueByKey(KeyEvent.keyCodeToString(keyCode));
                SingletonSharedPreference.removeValueByKey(screen.getPreference(i).getKey());
            }
        }
    }
}
