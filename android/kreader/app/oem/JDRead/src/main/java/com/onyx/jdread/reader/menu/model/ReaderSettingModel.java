package com.onyx.jdread.reader.menu.model;

import com.onyx.jdread.reader.menu.event.CloseReaderSettingMenuEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingModel {
    public enum ReaderSystemMenuGroup {
        progressMenuGroup, brightnessMenuGroup,textMenuGroup,imageMenuGroup,customMenuGroup
    }

    public void dismissZoneClick() {
        EventBus.getDefault().post(new CloseReaderSettingMenuEvent());
    }
}
