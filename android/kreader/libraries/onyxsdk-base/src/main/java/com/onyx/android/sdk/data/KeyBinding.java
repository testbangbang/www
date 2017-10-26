package com.onyx.android.sdk.data;

import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ming on 2017/4/11.
 */

public class KeyBinding {

    private Pair<String, CustomBindKeyBean>[] keyBindings = new Pair[] {
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_PAGE_DOWN), CustomBindKeyBean.createKeyBean("", KeyAction.NEXT_SCREEN)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_PAGE_UP), CustomBindKeyBean.createKeyBean("", KeyAction.PREV_SCREEN)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_VOLUME_DOWN), CustomBindKeyBean.createKeyBean("", KeyAction.NEXT_SCREEN)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_VOLUME_UP), CustomBindKeyBean.createKeyBean("", KeyAction.PREV_SCREEN)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_DPAD_RIGHT), CustomBindKeyBean.createKeyBean("", KeyAction.MOVE_RIGHT)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_DPAD_LEFT), CustomBindKeyBean.createKeyBean("", KeyAction.MOVE_LEFT)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_DPAD_UP), CustomBindKeyBean.createKeyBean("", KeyAction.MOVE_UP)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_DPAD_DOWN), CustomBindKeyBean.createKeyBean("", KeyAction.MOVE_DOWN)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_MENU), CustomBindKeyBean.createKeyBean("", KeyAction.SHOW_MENU)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_DPAD_CENTER), CustomBindKeyBean.createKeyBean("", KeyAction.TOGGLE_BOOKMARK)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_CLEAR), CustomBindKeyBean.createKeyBean("", KeyAction.CHANGE_TO_ERASE_MODE)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_ALT_LEFT), CustomBindKeyBean.createKeyBean("", KeyAction.CHANGE_TO_SCRIBBLE_MODE)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_ALT_RIGHT), CustomBindKeyBean.createKeyBean("", KeyAction.CHANGE_TO_SCRIBBLE_MODE)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_BUTTON_START), CustomBindKeyBean.createKeyBean("", KeyAction.CHANGE_TO_SCRIBBLE_MODE)),
            new Pair(KeyEvent.keyCodeToString(KeyEvent.KEYCODE_BACK), CustomBindKeyBean.createKeyBean("", KeyAction.CLOSE)),
    };

    private Map<String, CustomBindKeyBean> HandlerManager = new HashMap<>();

    public Map<String, CustomBindKeyBean> getHandlerManager() {
        return HandlerManager;
    }

    public void setHandlerManager(Map<String, CustomBindKeyBean> handlerManager) {
        HandlerManager = handlerManager;
    }

    public KeyBinding() {

    }

    private void useDefaultValue() {
        for (Pair<String, CustomBindKeyBean> binding : keyBindings) {
            HandlerManager.put(binding.first, binding.second);
        }
    }

    private static KeyBinding keyBinding;

    public static KeyBinding defaultValue() {
        if (keyBinding == null) {
            keyBinding = new KeyBinding();
            keyBinding.useDefaultValue();

        }
        return keyBinding;
    }
}
