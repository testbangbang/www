package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class UpdateViewSettingEvent {
    private ReaderTextStyle style;

    private ImageReflowSettings settings;

    private ReaderUserDataInfo readerUserDataInfo;

    public ReaderTextStyle getStyle() {
        return style;
    }

    public void setStyle(ReaderTextStyle style) {
        this.style = style;
    }

    public ImageReflowSettings getSettings() {
        return settings;
    }

    public void setSettings(ImageReflowSettings settings) {
        this.settings = settings;
    }

    public ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public void setReaderUserDataInfo(ReaderUserDataInfo readerUserDataInfo) {
        this.readerUserDataInfo = readerUserDataInfo;
    }
}
