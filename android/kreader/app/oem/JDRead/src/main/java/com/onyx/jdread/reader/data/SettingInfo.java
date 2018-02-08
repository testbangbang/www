package com.onyx.jdread.reader.data;

import com.onyx.jdread.reader.menu.common.ReaderConfig;

/**
 * Created by huxiaomao on 2018/2/7.
 */

public class SettingInfo {
    public int settingType = ReaderConfig.DEFAULT_SETTING_TYPE;
    public int settingStyle = ReaderConfig.DEFAULT_SETTING_TYPE;
    public int customLineSpacing = ReaderConfig.DEFAULT_CUSTOM_LINE_SPACING;
    public int customLeftAndRightMargin = ReaderConfig.DEFAULT_CUSTOM_LEFTANDRIGHT_MARGIN;
    public int customTopAndBottomMargin = ReaderConfig.DEFAULT_CUSTOM_TOPANDBOTTOM_MARGIN;
    public int customParagraphSpacing = ReaderConfig.DEFAULT_CUSTOM_PARAGRAPH_SPACING;

    public int getSettingType() {
        return settingType;
    }

    public void setSettingType(int settingType) {
        this.settingType = settingType;
    }

    public int getCustomLineSpacing() {
        return customLineSpacing;
    }

    public void setCustomLineSpacing(int customLineSpacing) {
        this.customLineSpacing = customLineSpacing;
    }

    public int getCustomLeftAndRightMargin() {
        return customLeftAndRightMargin;
    }

    public void setCustomLeftAndRightMargin(int customLeftAndRightMargin) {
        this.customLeftAndRightMargin = customLeftAndRightMargin;
    }

    public int getCustomTopAndBottomMargin() {
        return customTopAndBottomMargin;
    }

    public void setCustomTopAndBottomMargin(int customTopAndBottomMargin) {
        this.customTopAndBottomMargin = customTopAndBottomMargin;
    }

    public int getCustomParagraphSpacing() {
        return customParagraphSpacing;
    }

    public void setCustomParagraphSpacing(int customParagraphSpacing) {
        this.customParagraphSpacing = customParagraphSpacing;
    }

    public int getSettingStyle() {
        return settingStyle;
    }

    public void setSettingStyle(int settingStyle) {
        this.settingStyle = settingStyle;
    }
}
