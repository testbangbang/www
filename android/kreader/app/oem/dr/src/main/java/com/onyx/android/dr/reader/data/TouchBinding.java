package com.onyx.android.dr.reader.data;

import android.util.Pair;

import com.onyx.android.sdk.data.CustomBindKeyBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ming on 2017/4/11.
 */

public class TouchBinding {

    public static final String TOUCH_LEFT_TOP = "TOUCH_LEFT_TOP";
    public static final String TOUCH_LEFT_BOTTOM = "TOUCH_LEFT_BOTTOM";
    public static final String TOUCH_RIGHT_TOP = "TOUCH_RIGHT_TOP";
    public static final String TOUCH_RIGHT_BOTTOM = "TOUCH_RIGHT_BOTTOM";
    public static final String TOUCH_CENTER = "TOUCH_CENTER";

    private Map<String, CustomBindKeyBean> bindingMap = new HashMap<>();

    private static final Pair<String, CustomBindKeyBean>[] touchBindings = new Pair[] {
        new Pair(TOUCH_LEFT_TOP, CustomBindKeyBean.createKeyBean("", TouchAction.PREV_PAGE)),
        new Pair(TOUCH_LEFT_BOTTOM, CustomBindKeyBean.createKeyBean("", TouchAction.PREV_PAGE)),
        new Pair(TOUCH_RIGHT_TOP, CustomBindKeyBean.createKeyBean("", TouchAction.NEXT_PAGE)),
        new Pair(TOUCH_RIGHT_BOTTOM, CustomBindKeyBean.createKeyBean("", TouchAction.NEXT_PAGE)),
        new Pair(TOUCH_CENTER, CustomBindKeyBean.createKeyBean("", TouchAction.SHOW_MENU)),
    };

    public TouchBinding() {
        for (Pair<String, CustomBindKeyBean> binding : touchBindings) {
            bindingMap.put(binding.first, binding.second);
        }
    }

    public Map<String, CustomBindKeyBean> getTouchBindingMap() {
        return bindingMap;
    }

    private static TouchBinding touchBinding;

    public static TouchBinding defaultValue() {
        if (touchBinding == null) {
            touchBinding = new TouchBinding();
        }
        return touchBinding;
    }
}
