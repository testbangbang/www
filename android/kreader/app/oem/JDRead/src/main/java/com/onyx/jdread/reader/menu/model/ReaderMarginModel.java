package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.jdread.reader.data.SettingInfo;
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
    public final ObservableInt lineSpacing = new ObservableInt(ReaderConfig.DEFAULT_CUSTOM_LINE_SPACING);
    public final ObservableInt paragraphSpacing = new ObservableInt(ReaderConfig.DEFAULT_CUSTOM_PARAGRAPH_SPACING);
    public final ObservableInt leftAndRightSpacing = new ObservableInt(ReaderConfig.DEFAULT_CUSTOM_LEFTANDRIGHT_MARGIN);
    public final ObservableInt upAndDownSpacing = new ObservableInt(ReaderConfig.DEFAULT_CUSTOM_TOPANDBOTTOM_MARGIN);
    private SettingInfo settingInfo;
    private EventBus eventBus;

    public ReaderMarginModel(EventBus eventBus, SettingInfo settingInfo) {
        this.eventBus = eventBus;
        setDefaultStyle(settingInfo);
    }

    private void setDefaultStyle(SettingInfo settingInfo) {
        setLineSpacing(settingInfo.customLineSpacing);
        setParagraphSpacing(settingInfo.customParagraphSpacing);
        setUpAndDownSpacing(settingInfo.customTopAndBottomMargin);
        setLeftAndRightSpacing(settingInfo.customLeftAndRightMargin);
        this.settingInfo = settingInfo;
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
        setLineSpacingProgress(ReaderConfig.SETTING_ONE_STYLE_KEY);
    }

    public void onLineSpacingTwoClick() {
        setLineSpacingProgress(ReaderConfig.SETTING_TWO_STYLE_KEY);
    }

    public void onLineSpacingThreeClick() {
        setLineSpacingProgress(ReaderConfig.SETTING_THREE_STYLE_KEY);
    }

    public void onLineSpacingFourClick() {
        setLineSpacingProgress(ReaderConfig.SETTING_FOUR_STYLE_KEY);
    }

    public void onLineSpacingFiveClick() {
        setLineSpacingProgress(ReaderConfig.SETTING_FIVE_STYLE_KEY);
    }

    public void onLineSpacingSixClick() {
        setLineSpacingProgress(ReaderConfig.SETTING_SIX_STYLE_KEY);
    }

    public void setLineSpacingProgress(int lineSpacing) {
        if (this.lineSpacing.get() == lineSpacing) {
            return;
        }
        setLineSpacing(lineSpacing);

        settingInfo.settingType = ReaderConfig.SETTING_TYPE_CUSTOM;
        settingInfo.customLineSpacing = lineSpacing;

        SettingLineSpacingEvent event = new SettingLineSpacingEvent();
        event.margin = ReaderConfig.customLineSpacing.get(lineSpacing);
        getEventBus().post(event);
    }

    public ObservableInt getParagraphSpacing() {
        return paragraphSpacing;
    }

    public void setParagraphSpacing(int paragraphSpacing) {
        this.paragraphSpacing.set(paragraphSpacing);
    }

    public void onParagraphSpacingOneClick() {
        applyParagraphSpacing(ReaderConfig.SETTING_ONE_STYLE_KEY);
    }

    public void onParagraphSpacingTwoClick() {
        applyParagraphSpacing(ReaderConfig.SETTING_TWO_STYLE_KEY);
    }

    public void onParagraphSpacingThreeClick() {
        applyParagraphSpacing(ReaderConfig.SETTING_THREE_STYLE_KEY);
    }

    public void onParagraphSpacingFourClick() {
        applyParagraphSpacing(ReaderConfig.SETTING_FOUR_STYLE_KEY);
    }

    public void onParagraphSpacingFiveClick() {
        applyParagraphSpacing(ReaderConfig.SETTING_FIVE_STYLE_KEY);
    }

    public void onParagraphSpacingSixClick() {
        applyParagraphSpacing(ReaderConfig.SETTING_SIX_STYLE_KEY);
    }

    public void applyParagraphSpacing(int paragraphSpacing) {
        if (this.paragraphSpacing.get() == paragraphSpacing) {
            return;
        }
        setParagraphSpacing(paragraphSpacing);

        settingInfo.settingType = ReaderConfig.SETTING_TYPE_CUSTOM;
        settingInfo.customParagraphSpacing = paragraphSpacing;

        SettingParagraphSpacingEvent event = new SettingParagraphSpacingEvent();
        event.spacing = ReaderConfig.customParagraphSpacing.get(paragraphSpacing);
        getEventBus().post(event);
    }

    public ObservableInt getLeftAndRightSpacing() {
        return leftAndRightSpacing;
    }

    public void setLeftAndRightSpacing(int leftAndRightSpacing) {
        this.leftAndRightSpacing.set(leftAndRightSpacing);
    }

    public void onLeftAndRightSpacingOneClick() {
        setLeftAndRightProgress(ReaderConfig.SETTING_ONE_STYLE_KEY);
    }

    public void onLeftAndRightSpacingTwoClick() {
        setLeftAndRightProgress(ReaderConfig.SETTING_TWO_STYLE_KEY);
    }

    public void onLeftAndRightSpacingThreeClick() {
        setLeftAndRightProgress(ReaderConfig.SETTING_THREE_STYLE_KEY);
    }

    public void onLeftAndRightSpacingFourClick() {
        setLeftAndRightProgress(ReaderConfig.SETTING_FOUR_STYLE_KEY);
    }

    public void onLeftAndRightSpacingFiveClick() {
        setLeftAndRightProgress(ReaderConfig.SETTING_FIVE_STYLE_KEY);
    }

    public void onLeftAndRightSpacingSixClick() {
        setLeftAndRightProgress(ReaderConfig.SETTING_SIX_STYLE_KEY);
    }

    public void setLeftAndRightProgress(int spacing) {
        if (getLeftAndRightSpacing().get() == spacing) {
            return;
        }
        setLeftAndRightSpacing(spacing);

        settingInfo.settingType = ReaderConfig.SETTING_TYPE_CUSTOM;
        settingInfo.customLeftAndRightMargin = spacing;

        SettingLeftAndRightSpacingEvent event = new SettingLeftAndRightSpacingEvent();
        ReaderConfig.LeftAndRight leftAndRight = ReaderConfig.getCustomLeftAndRightMargin(settingInfo.settingStyle,spacing);
        event.margin = leftAndRight.left;
        getEventBus().post(event);
    }

    public ObservableInt getUpAndDownSpacing() {
        return upAndDownSpacing;
    }

    public void setUpAndDownSpacing(int spacing) {
        this.upAndDownSpacing.set(spacing);
    }

    public void onUpAndDownSpacingOneClick() {
        setUpAndDownProgress(ReaderConfig.SETTING_ONE_STYLE_KEY);
    }

    public void onUpAndDownSpacingTwoClick() {
        setUpAndDownProgress(ReaderConfig.SETTING_TWO_STYLE_KEY);
    }

    public void onUpAndDownSpacingThreeClick() {
        setUpAndDownProgress(ReaderConfig.SETTING_THREE_STYLE_KEY);
    }

    public void onUpAndDownSpacingFourClick() {
        setUpAndDownProgress(ReaderConfig.SETTING_FOUR_STYLE_KEY);
    }

    public void onUpAndDownSpacingFiveClick() {
        setUpAndDownProgress(ReaderConfig.SETTING_FIVE_STYLE_KEY);
    }

    public void onUpAndDownSpacingSixClick() {
        setUpAndDownProgress(ReaderConfig.SETTING_SIX_STYLE_KEY);
    }

    public void setUpAndDownProgress(int spacing) {
        if (this.upAndDownSpacing.get() == spacing) {
            return;
        }
        setUpAndDownSpacing(spacing);

        settingInfo.settingType = ReaderConfig.SETTING_TYPE_CUSTOM;
        settingInfo.customTopAndBottomMargin = spacing;

        SettingUpAndDownSpacingEvent event = new SettingUpAndDownSpacingEvent();
        ReaderConfig.TopAndBottom topAndBottom = ReaderConfig.getCustomTopAndBottomMargin(settingInfo.settingStyle,spacing);
        event.top = topAndBottom.top;
        event.bottom = topAndBottom.bottom;
        getEventBus().post(event);
    }
}
