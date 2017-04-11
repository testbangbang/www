package com.onyx.kreader.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.KeyAction;
import com.onyx.android.sdk.data.KeyBinding;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.handler.HandlerManager;

import java.util.Map;

public class ControlSettingsActivity extends PreferenceActivity {

    private Pair<String, Integer>[] keyActions = new Pair[] {
       new Pair(KeyAction.NEXT_PAGE, R.string.settings_key_binding_next_page_tittle),
       new Pair(KeyAction.PREV_PAGE, R.string.settings_key_binding_prev_page_tittle),
       new Pair(KeyAction.NEXT_SCREEN, R.string.settings_key_binding_next_view_tittle),
       new Pair(KeyAction.PREV_SCREEN, R.string.settings_key_binding_prev_view_tittle),
       new Pair(KeyAction.MOVE_LEFT, R.string.settings_key_binding_move_left_tittle),
       new Pair(KeyAction.MOVE_RIGHT, R.string.settings_key_binding_move_right_tittle),
       new Pair(KeyAction.MOVE_UP, R.string.settings_key_binding_move_up_tittle),
       new Pair(KeyAction.MOVE_DOWN, R.string.settings_key_binding_move_down_tittle),
       new Pair(KeyAction.SHOW_MENU, R.string.settings_key_binding_show_menu_tittle),
       new Pair(KeyAction.INCREASE_FONT_SIZE, R.string.settings_key_binding_increase_font_size_tittle),
       new Pair(KeyAction.DECREASE_FONT_SIZE, R.string.settings_key_binding_decrease_font_size_tittle),
       new Pair(KeyAction.TOGGLE_BOOKMARK, R.string.settings_key_binding_toggle_bookmark_tittle),
       new Pair(KeyAction.CHANGE_TO_ERASE_MODE, R.string.settings_key_binding_change_to_erase_mode_tittle),
       new Pair(KeyAction.CHANGE_TO_SCRIBBLE_MODE, R.string.settings_key_binding_change_to_scribble_mode_tittle),
    };
    private Map<String, CustomBindKeyBean> bindingMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.key_settings);
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
        initPreference();
    }

    private void initPreference() {
        KeyBinding keyBinding = DeviceConfig.sharedInstance(this).getKeyBinding();
        if (keyBinding == null) {
            return;
        }
        bindingMap = keyBinding.getHandlerManager();
        for (Map.Entry<String, CustomBindKeyBean> stringJSONObjectEntry : bindingMap.entrySet()) {
            String keyCode = stringJSONObjectEntry.getKey();
            String keyBeanJsonString = SingletonSharedPreference.getStringValue(keyCode);
            String action = "";
            if (!StringUtils.isNullOrEmpty(keyBeanJsonString)) {
                CustomBindKeyBean bindKeyBean = JSONObject.parseObject(keyBeanJsonString, CustomBindKeyBean.class);
                action = bindKeyBean.getAction();
            }else {
                CustomBindKeyBean object = stringJSONObjectEntry.getValue();
                if (object != null) {
                    action = object.getAction();
                }
            }
            Preference preference = findPreference(keyCode);
            if (preference != null) {
                preference.setSummary(getActionTitle(action));
            }
        }
    }

    private String getActionTitle(final String action) {
        String title = "";
        for (Pair<String, Integer> keyAction : keyActions) {
            if (keyAction.first.equals(action)) {
                title = getString(keyAction.second);
            }
        }
        return title;
    }

    @Override
    public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, final Preference preference) {
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
        chooseKeyFunction(preference);
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void chooseKeyFunction(final Preference preference) {
        if (bindingMap == null) {
            return;
        }
        final String keyCode = preference.getKey();
        final CustomBindKeyBean object = bindingMap.get(keyCode);
        if (object == null) {
            return;
        }
        String action = object.getAction();
        final String[] items = new String[keyActions.length];
        int checkedItem = 0;
        for (int i = 0; i < keyActions.length; i++) {
            items[i] = getString(keyActions[i].second);
            if (action.equals(keyActions[i].first)) {
                checkedItem = i;
            }
        }
        new AlertDialog.Builder(ControlSettingsActivity.this)
                .setTitle(R.string.setting_custom_function_key)
                .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedAction = keyActions[which].first;
                        CustomBindKeyBean bean = new CustomBindKeyBean(object.getArgs(), selectedAction);
                        SingletonSharedPreference.setStringValue(keyCode, JSON.toJSONString(bean));
                        SingletonSharedPreference.setIntValue(selectedAction, KeyEvent.keyCodeFromString(keyCode));
                        preference.setSummary(getActionTitle(selectedAction));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
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
            String keyCode = screen.getPreference(i).getKey();
            String keyBeanJsonString = SingletonSharedPreference.getStringValue(keyCode);
            if (!StringUtils.isNullOrEmpty(keyBeanJsonString)) {
                CustomBindKeyBean bindKeyBean = JSONObject.parseObject(keyBeanJsonString, CustomBindKeyBean.class);
                SingletonSharedPreference.removeValueByKey(keyCode);
                SingletonSharedPreference.removeValueByKey(bindKeyBean.getAction());
            }
        }
        initPreference();
    }
}
