package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.event.SettingLeftAndRightSpacingEvent;
import com.onyx.jdread.reader.menu.event.SettingLineSpacingEvent;
import com.onyx.jdread.reader.menu.event.SettingParagraphSpacingEvent;
import com.onyx.jdread.reader.menu.event.SettingUpAndDownSpacingEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderCustomizeModel {
    public final ObservableInt lineSpacingProgress = new ObservableInt(0);
    public final ObservableInt maxLineSpacing = new ObservableInt(ReaderConfig.PageLineSpacing.MAX_LINE_SPACING);

    public final ObservableInt segmentProgress = new ObservableInt();
    public final ObservableInt maxSegmentSpacing = new ObservableInt(ReaderConfig.PageSegmentSpacing.MAX_SEGMENT_SPACING);

    public final ObservableInt leftAndRightProgress = new ObservableInt();
    public final ObservableInt maxLeftAndRightSpacing = new ObservableInt(ReaderConfig.PageLeftAndRightSpacing.MAX_LEFT_AND_RIGHT_SPACING);

    public final ObservableInt upAndDownProgress = new ObservableInt();
    public final ObservableInt maxUpAndDownSpacing = new ObservableInt(ReaderConfig.PageUpAndDownSpacing.MAX_UP_AND_DOWN_SPACING);

    private ObservableBoolean isShow = new ObservableBoolean(false);

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public ObservableInt getLineSpacingProgress() {
        return lineSpacingProgress;
    }

    public void setLineSpacingProgress(int progress){
        SettingLineSpacingEvent event = new SettingLineSpacingEvent();
        event.margin = progress;
        EventBus.getDefault().post(event);
    }

    public ObservableInt getSegmentProgress() {
        return segmentProgress;
    }

    public void setSegmentProgress(int progress){
        SettingParagraphSpacingEvent event = new SettingParagraphSpacingEvent();
        event.margin = progress;
        EventBus.getDefault().post(event);
    }

    public ObservableInt getLeftAndRightProgress() {
        return leftAndRightProgress;
    }

    public void setLeftAndRightProgress(int progress){
        SettingLeftAndRightSpacingEvent event = new SettingLeftAndRightSpacingEvent();
        event.margin = progress;
        EventBus.getDefault().post(event);
    }

    public ObservableInt getUpAndDownProgress() {
        return upAndDownProgress;
    }

    public void setUpAndDownProgress(int progress){
        SettingUpAndDownSpacingEvent event = new SettingUpAndDownSpacingEvent();
        event.margin = progress;
        EventBus.getDefault().post(event);
    }
}
