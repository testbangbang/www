package com.onyx.kreader.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.onyx.android.sdk.data.ControlType;
import com.onyx.android.sdk.data.KeyAction;
import com.onyx.android.sdk.data.KeyBinding;
import com.onyx.android.sdk.data.TouchAction;
import com.onyx.android.sdk.data.TouchBinding;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

import java.util.List;
import java.util.Map;

public class ControlSettingsActivity extends PreferenceActivity {

    public static final String CONTROL_TYPE = "control_type";
    public static final String CONTROL_KEY = "control_key";
    public static final String CONTROL_TOUCH = "control_touch";

    private Pair<String, Integer>[] keyActions = new Pair[]{
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
    };

    private Pair<String, Integer>[] touchActions = new Pair[] {
            new Pair(TouchAction.NEXT_PAGE, R.string.settings_key_binding_next_page_tittle),
            new Pair(TouchAction.PREV_PAGE, R.string.settings_key_binding_prev_page_tittle),
            new Pair(TouchAction.INCREASE_BRIGHTNESS, R.string.settings_touch_binding_increase_brightness_tittle),
            new Pair(TouchAction.DECREASE_BRIGHTNESS, R.string.settings_touch_binding_decrease_brightness_tittle),
            new Pair(TouchAction.TOGGLE_FULLSCREEN, R.string.settings_touch_binding_toggle_fullscreen_tittle),
            new Pair(TouchAction.OPEN_TTS, R.string.settings_touch_binding_open_tts_tittle),
            new Pair(TouchAction.AUTO_PAGE, R.string.settings_touch_binding_auto_page_tittle),
            new Pair(TouchAction.NEXT_TEN_PAGE, R.string.settings_touch_binding_next_ten_page_tittle),
            new Pair(TouchAction.PREV_TEN_PAGE, R.string.settings_touch_binding_prev_ten_page_tittle),
            new Pair(TouchAction.TOGGLE_A2, R.string.settings_touch_binding_toggle_a2_tittle),
    };

    private Map<String, CustomBindKeyBean> bindingMap;
    private Pair<String, Integer>[] controlActions;
    private ControlType controlType = ControlType.KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initData();
        addPreferencesFromResource(isKeySetting() ? R.xml.key_settings : R.xml.touch_settings);
        setContentView(R.layout.setting_main);
        RelativeLayout mBackFunctionLayout = (RelativeLayout) findViewById(R.id.back_function_layout);
        TextView settingTittle = (TextView) findViewById(R.id.settingTittle);
        settingTittle.setText(isKeySetting() ? R.string.settings_control_tittle : R.string.settings_touch_tittle);
        mBackFunctionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initPreference();
    }

    private void initData() {
        Intent intent = getIntent();
        controlType = intent.getStringExtra(CONTROL_TYPE).equals(CONTROL_TOUCH) ? ControlType.TOUCH : ControlType.KEY;
        controlActions = isKeySetting() ? keyActions : touchActions;
    }

    public Map<String, CustomBindKeyBean> getBindingMap() {
        if (bindingMap == null) {
            bindingMap = isKeySetting() ? getDeviceConfig().getKeyBinding().getHandlerManager() :
                    getDeviceConfig().getTouchBinding().getBindingMap();
        }
        return bindingMap;
    }

    private boolean isKeySetting() {
        return controlType == ControlType.KEY;
    }

    public Map<String, CustomBindKeyBean> getDefaultBindingMap() {
        return isKeySetting() ? KeyBinding.defaultValue().getHandlerManager() :
                TouchBinding.defaultValue().getBindingMap();
    }

    private DeviceConfig getDeviceConfig() {
        return DeviceConfig.sharedInstance(this);
    }

    private void initPreference() {
        List<String> keysForSetting = DeviceConfig.sharedInstance(this).getKeysForSetting();
        for (Map.Entry<String, CustomBindKeyBean> entry : getBindingMap().entrySet()) {
            String keyCode = entry.getKey();
            CustomBindKeyBean bindKeyBean = getBindKeyBean(keyCode);
            if (bindKeyBean == null) {
                bindKeyBean = entry.getValue();
            }
            String action = bindKeyBean.getAction();
            Preference preference = findPreference(keyCode);
            if (preference != null) {
                preference.setSummary(getActionTitle(action));
                if (isKeySetting() && keysForSetting != null && !keysForSetting.contains(keyCode)) {
                    getPreferenceScreen().removePreference(preference);
                }
            }
        }
    }

    private CustomBindKeyBean getBindKeyBean(final String code) {
        String keyBeanJsonString = SingletonSharedPreference.getStringValue(code);
        if (!StringUtils.isNullOrEmpty(keyBeanJsonString)) {
            return JSONObject.parseObject(keyBeanJsonString, CustomBindKeyBean.class);
        }
        return null;
    }

    private String getActionTitle(final String action) {
        String title = "";
        for (Pair<String, Integer> keyAction : controlActions) {
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
        if (getBindingMap() == null) {
            return;
        }
        final String keyCode = preference.getKey();
        CustomBindKeyBean preferenceBindKeyBean = getBindKeyBean(keyCode);
        final CustomBindKeyBean bindKeyBean = (preferenceBindKeyBean == null
                || StringUtils.isNullOrEmpty(preferenceBindKeyBean.getAction()))
                ? getBindingMap().get(keyCode)
                : preferenceBindKeyBean;
        if (bindKeyBean == null) {
            return;
        }
        String action = bindKeyBean.getAction();
        final String[] items = new String[controlActions.length];
        int checkedItem = 0;
        for (int i = 0; i < controlActions.length; i++) {
            items[i] = getString(controlActions[i].second);
            if (action.equals(controlActions[i].first)) {
                checkedItem = i;
            }
        }
        new AlertDialog.Builder(ControlSettingsActivity.this)
                .setTitle(R.string.setting_custom_function_key)
                .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedAction = controlActions[which].first;
                        CustomBindKeyBean bean = new CustomBindKeyBean(bindKeyBean.getArgs(), selectedAction);
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
