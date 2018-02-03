package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
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
    public final ObservableInt lineSpacing = new ObservableInt(LINE_SPACING_TWO);
    public final ObservableInt paragraphSpacing = new ObservableInt(PARAGRAPH_SPACING_THREE);
    public final ObservableInt leftAndRightSpacing = new ObservableInt(LEFT_AND_RIGHT_SPACING_FOUR);
    public final ObservableInt upAndDownSpacing = new ObservableInt(UP_AND_DOWN_SPACING_THREE);


    public static final int LINE_SPACING_ONE = 10;
    public static final int LINE_SPACING_TWO = 20;
    public static final int LINE_SPACING_THREE = 30;
    public static final int LINE_SPACING_FOUR = 40;
    public static final int LINE_SPACING_FIVE = 50;
    public static final int LINE_SPACING_SIX = 60;

    public static final int PARAGRAPH_SPACING_ONE = 20;
    public static final int PARAGRAPH_SPACING_TWO = 50;
    public static final int PARAGRAPH_SPACING_THREE = 80;
    public static final int PARAGRAPH_SPACING_FOUR = 110;
    public static final int PARAGRAPH_SPACING_FIVE = 140;
    public static final int PARAGRAPH_SPACING_SIX = 170;



    public static final int  LEFT_AND_RIGHT_SPACING_ONE = 3;
    public static final int  LEFT_AND_RIGHT_SPACING_TWO = 6;
    public static final int LEFT_AND_RIGHT_SPACING_THREE = 9;
    public static final int LEFT_AND_RIGHT_SPACING_FOUR = 12;
    public static final int  LEFT_AND_RIGHT_SPACING_FIVE = 15;
    public static final int LEFT_AND_RIGHT_SPACING_SIX = 18;


    public static final int UP_AND_DOWN_SPACING_ONE = 3;
    public static final int UP_AND_DOWN_SPACING_TWO = 6;
    public static final int UP_AND_DOWN_SPACING_THREE = 9;
    public static final int UP_AND_DOWN_SPACING_FOUR = 12;
    public static final int UP_AND_DOWN_SPACING_FIVE = 15;
    public static final int UP_AND_DOWN_SPACING_SIX = 18;


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


    public ObservableInt getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing.set(lineSpacing);
    }

    public void onLineSpacingOneClick() {
        setLineSpacingProgress(LINE_SPACING_ONE);
    }

    public void onLineSpacingTwoClick() {
        setLineSpacing(LINE_SPACING_TWO);
    }

    public void onLineSpacingThreeClick() {
        setLineSpacingProgress(LINE_SPACING_THREE);
    }

    public void onLineSpacingFourClick() {
        setLineSpacing(LINE_SPACING_FOUR);
    }

    public void onLineSpacingFiveClick() {
        setLineSpacingProgress(LINE_SPACING_FIVE);
    }

    public void onLineSpacingSixClick() {
        setLineSpacingProgress(LINE_SPACING_SIX);
    }

    public void setLineSpacingProgress(int lineSpacing) {
        if (this.lineSpacing.get() == lineSpacing) {
            return;
        }
        setLineSpacing(lineSpacing);
        SettingLineSpacingEvent event = new SettingLineSpacingEvent();
        event.margin = lineSpacing;
        getEventBus().post(event);
    }

    public ObservableInt getParagraphSpacing() {
        return paragraphSpacing;
    }

    public void setParagraphSpacing(int paragraphSpacing) {
        this.paragraphSpacing.set(paragraphSpacing);
    }

    public void onParagraphSpacingOneClick() {
        setSegmentProgress(PARAGRAPH_SPACING_ONE);
    }

    public void onParagraphSpacingTwoClick() {
        setSegmentProgress(PARAGRAPH_SPACING_TWO);
    }

    public void onParagraphSpacingThreeClick() {
        setSegmentProgress(PARAGRAPH_SPACING_THREE);
    }

    public void onParagraphSpacingFourClick() {
        setSegmentProgress(PARAGRAPH_SPACING_FOUR);
    }

    public void onParagraphSpacingFiveClick() {
        setSegmentProgress(PARAGRAPH_SPACING_FIVE);
    }

    public void onParagraphSpacingSixClick() {
        setSegmentProgress(PARAGRAPH_SPACING_SIX);
    }

    public void setSegmentProgress(int paragraphSpacing) {
        if(this.paragraphSpacing.get() == paragraphSpacing){
            return;
        }
        setParagraphSpacing(paragraphSpacing);
        SettingParagraphSpacingEvent event = new SettingParagraphSpacingEvent();
        event.spacing = paragraphSpacing;
        getEventBus().post(event);
    }

    public ObservableInt getLeftAndRightSpacing() {
        return leftAndRightSpacing;
    }

    public void setLeftAndRightSpacing(int leftAndRightSpacing){
        this.leftAndRightSpacing.set(leftAndRightSpacing);
    }

    public void onLeftAndRightSpacingOneClick() {
        setLeftAndRightProgress(LEFT_AND_RIGHT_SPACING_ONE);
    }

    public void onLeftAndRightSpacingTwoClick() {
        setLeftAndRightProgress(LEFT_AND_RIGHT_SPACING_TWO);
    }

    public void onLeftAndRightSpacingThreeClick() {
        setLeftAndRightProgress(LEFT_AND_RIGHT_SPACING_THREE);
    }

    public void onLeftAndRightSpacingFourClick() {
        setLeftAndRightProgress(LEFT_AND_RIGHT_SPACING_FOUR);
    }

    public void onLeftAndRightSpacingFiveClick() {
        setLeftAndRightProgress(LEFT_AND_RIGHT_SPACING_FIVE);
    }

    public void onLeftAndRightSpacingSixClick() {
        setLeftAndRightProgress(LEFT_AND_RIGHT_SPACING_SIX);
    }

    public void setLeftAndRightProgress(int spacing) {
        if(getLeftAndRightSpacing().get() == spacing){
            return;
        }
        setLeftAndRightSpacing(spacing);
        SettingLeftAndRightSpacingEvent event = new SettingLeftAndRightSpacingEvent();
        event.margin = spacing;
        getEventBus().post(event);
    }

    public ObservableInt getUpAndDownSpacing() {
        return upAndDownSpacing;
    }

    public void setUpAndDownSpacing(int spacing){
        this.upAndDownSpacing.set(spacing);
    }

    public void onUpAndDownSpacingOneClick() {
        setUpAndDownProgress(UP_AND_DOWN_SPACING_ONE);
    }

    public void onUpAndDownSpacingTwoClick() {
        setUpAndDownProgress(UP_AND_DOWN_SPACING_TWO);
    }

    public void onUpAndDownSpacingThreeClick() {
        setUpAndDownProgress(UP_AND_DOWN_SPACING_THREE);
    }

    public void onUpAndDownSpacingFourClick() {
        setUpAndDownProgress(UP_AND_DOWN_SPACING_FOUR);
    }

    public void onUpAndDownSpacingFiveClick() {
        setUpAndDownProgress(UP_AND_DOWN_SPACING_FIVE);
    }

    public void onUpAndDownSpacingSixClick() {
        setUpAndDownProgress(UP_AND_DOWN_SPACING_SIX);
    }

    public void setUpAndDownProgress(int spacing) {
        if(this.upAndDownSpacing.get() == spacing){
            return;
        }
        setUpAndDownSpacing(spacing);
        SettingUpAndDownSpacingEvent event = new SettingUpAndDownSpacingEvent();
        event.margin = spacing;
        getEventBus().post(event);
    }
}
