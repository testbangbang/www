package com.onyx.jdread.setting.event;

import com.onyx.jdread.setting.model.PswSettingData;

/**
 * Created by suicheng on 2018/2/7.
 */
public class PasswordSettingEvent {

    public PswSettingData data;

    public PasswordSettingEvent(PswSettingData data) {
        this.data = data;
    }
}
