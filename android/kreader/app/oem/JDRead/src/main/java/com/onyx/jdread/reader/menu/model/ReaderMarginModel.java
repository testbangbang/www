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

public class ReaderMarginModel {
    public final ObservableInt lineSpacingProgress = new ObservableInt(0);
    public final ObservableInt maxLineSpacing = new ObservableInt(ReaderConfig.PageLineSpacing.MAX_LINE_SPACING);

    public final ObservableInt segmentProgress = new ObservableInt();
    public final ObservableInt maxSegmentSpacing = new ObservableInt(ReaderConfig.PageSegmentSpacing.MAX_SEGMENT_SPACING);

    public final ObservableInt leftAndRightProgress = new ObservableInt();
    public final ObservableInt maxLeftAndRightSpacing = new ObservableInt(ReaderConfig.PageLeftAndRightSpacing.MAX_LEFT_AND_RIGHT_SPACING);

    public final ObservableInt upAndDownProgress = new ObservableInt();
    public final ObservableInt maxUpAndDownSpacing = new ObservableInt(ReaderConfig.PageUpAndDownSpacing.MAX_UP_AND_DOWN_SPACING);

    private EventBus eventBus;

    public ReaderMarginModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

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
        getEventBus().post(event);
    }

    public ObservableInt getSegmentProgress() {
        return segmentProgress;
    }

    public void setSegmentProgress(int progress){
        SettingParagraphSpacingEvent event = new SettingParagraphSpacingEvent();
        event.margin = progress;
        getEventBus().post(event);
    }

    public ObservableInt getLeftAndRightProgress() {
        return leftAndRightProgress;
    }

    public void setLeftAndRightProgress(int progress){
        SettingLeftAndRightSpacingEvent event = new SettingLeftAndRightSpacingEvent();
        event.margin = progress;
        getEventBus().post(event);
    }

    public ObservableInt getUpAndDownProgress() {
        return upAndDownProgress;
    }

    public void setUpAndDownProgress(int progress){
        SettingUpAndDownSpacingEvent event = new SettingUpAndDownSpacingEvent();
        event.margin = progress;
        getEventBus().post(event);
    }

    public void onLineSpacingMinusClick(){
        if(lineSpacingProgress.get() > 0){
            int lineSpacing = lineSpacingProgress.get();
            lineSpacingProgress.set(--lineSpacing);
            setLineSpacingProgress(lineSpacing);
        }
    }

    public void onLineSpacingPlusClick(){
        if(lineSpacingProgress.get() < ReaderConfig.PageLineSpacing.MAX_LINE_SPACING){
            int lineSpacing = lineSpacingProgress.get();
            lineSpacingProgress.set(++lineSpacing);
            setLineSpacingProgress(lineSpacing);
        }
    }

    public void onLineSegmentMinusClick(){
        if(segmentProgress.get() > 0){
            int lineSpacing = segmentProgress.get();
            segmentProgress.set(--lineSpacing);
            setSegmentProgress(lineSpacing);
        }
    }

    public void onLineSegmentPlusClick(){
        if(segmentProgress.get() < ReaderConfig.PageLineSpacing.MAX_LINE_SPACING){
            int lineSpacing = segmentProgress.get();
            segmentProgress.set(++lineSpacing);
            setSegmentProgress(lineSpacing);
        }
    }

    public void onLineLeftAndRightMinusClick(){
        if(leftAndRightProgress.get() > 0){
            int lineSpacing = leftAndRightProgress.get();
            leftAndRightProgress.set(--lineSpacing);
            setLeftAndRightProgress(lineSpacing);
        }
    }

    public void onLineLeftAndRightPlusClick(){
        if(leftAndRightProgress.get() < ReaderConfig.PageLineSpacing.MAX_LINE_SPACING){
            int lineSpacing = leftAndRightProgress.get();
            leftAndRightProgress.set(++lineSpacing);
            setLeftAndRightProgress(lineSpacing);
        }
    }

    public void onLineUpAndDownMinusClick(){
        if(upAndDownProgress.get() > 0){
            int lineSpacing = upAndDownProgress.get();
            upAndDownProgress.set(--lineSpacing);
            setUpAndDownProgress(lineSpacing);
        }
    }

    public void onLineUpAndDownPlusClick(){
        if(upAndDownProgress.get() < ReaderConfig.PageLineSpacing.MAX_LINE_SPACING){
            int lineSpacing = upAndDownProgress.get();
            upAndDownProgress.set(++lineSpacing);
            setUpAndDownProgress(lineSpacing);
        }
    }
}
