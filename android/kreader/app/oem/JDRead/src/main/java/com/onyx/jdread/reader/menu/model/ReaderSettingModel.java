package com.onyx.jdread.reader.menu.model;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.CloseReaderSettingMenuEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingModel {
    private ReaderDataHolder readerDataHolder;

    public enum ReaderSystemMenuGroup {
        progressMenuGroup, brightnessMenuGroup, textMenuGroup, imageMenuGroup, customMenuGroup
    }

    public ReaderSettingModel(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public void dismissZoneClick() {
        readerDataHolder.getEventBus().post(new CloseReaderSettingMenuEvent());
    }
}
