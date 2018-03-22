package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.reader.data.SettingInfo;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.event.ChangeChineseConvertTypeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingFontSizeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemBackPdfEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemCustomizeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingTypefaceEvent;

import org.greenrobot.eventbus.EventBus;

import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.TypefaceFour;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.TypefaceOne;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.TypefaceThree;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.TypefaceTwo;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderTextModel {
    private ObservableBoolean isShow = new ObservableBoolean(false);
    private ObservableBoolean isPdf = new ObservableBoolean(false);
    private ObservableField<ReaderTypeface> currentTypeface = new ObservableField<>(TypefaceOne);
    private ObservableInt currentFontSize = new ObservableInt(ReaderConfig.DEFAULT_PRESET_STYLE);
    private ObservableField<Language> currentLanguage = new ObservableField<>(Language.Simplified);
    private ObservableInt currentSettingType = new ObservableInt(ReaderConfig.DEFAULT_SETTING_TYPE);
    private EventBus eventBus;
    private SettingInfo settingInfo;
    private ObservableField<String> settingTitle = new ObservableField<>();

    public ReaderTextModel(EventBus eventBus, ReaderTextStyle style, SettingInfo settingInfo) {
        this.eventBus = eventBus;
        setDefaultStyle(style, settingInfo);
    }

    public void setDefaultStyle(ReaderTextStyle style, SettingInfo settingInfo) {
        if(style != null) {
            currentFontSize.set(settingInfo.settingStyle);
            currentSettingType.set(settingInfo.settingType);
            ReaderChineseConvertType chineseConvertType = ReaderConfig.getReaderChineseConvertType();
            setDefaultTypeFace(style.getFontFace());
            setDefaultLanguage(chineseConvertType);
            this.settingInfo = settingInfo;
        }
    }


    public ObservableField<String> getSettingTitle() {
        return settingTitle;
    }

    public void setSettingTitle(String settingTitle) {
        this.settingTitle.set(settingTitle);
    }

    public ObservableInt getCurrentSettingType() {
        return currentSettingType;
    }

    public void setCurrentSettingType(int currentSettingType) {
        this.currentSettingType.set(currentSettingType);
    }

    public void setDefaultLanguage(ReaderChineseConvertType chineseConvertType) {
        if (chineseConvertType == ReaderChineseConvertType.NONE || chineseConvertType == ReaderChineseConvertType.TRADITIONAL_TO_SIMPLIFIED) {
            setCurrentLanguage(Language.Simplified);
        } else {
            setCurrentLanguage(Language.Traditional);
        }
    }

    public void setDefaultTypeFace(String typeFace) {
        switch (typeFace) {
            case ReaderConfig.Typeface.TYPEFACE_ONE:
                setCurrentTypeface(TypefaceOne);
                break;
            case ReaderConfig.Typeface.TYPEFACE_TWO:
                setCurrentTypeface(TypefaceTwo);
                break;
            case ReaderConfig.Typeface.TYPEFACE_THREE:
                setCurrentTypeface(TypefaceThree);
                break;
            case ReaderConfig.Typeface.TYPEFACE_FOUR:
                setCurrentTypeface(TypefaceFour);
                break;
            default:
                break;
        }
    }

    public enum Language {
        Simplified, Traditional
    }

    public enum ReaderTypeface {
        TypefaceOne, TypefaceTwo, TypefaceThree, TypefaceFour
    }

    public ObservableField<Language> getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(Language language) {
        this.currentLanguage.set(language);
    }

    public ObservableField<ReaderTypeface> getCurrentTypeface() {
        return currentTypeface;
    }

    public boolean setCurrentTypeface(ReaderTypeface typeface) {
        if (currentTypeface.get() == typeface) {
            return false;
        }
        this.currentTypeface.set(typeface);
        return true;
    }

    public ObservableInt getCurrentFontSize() {
        return currentFontSize;
    }

    public void setCurrentFontSize(int style) {
        if(currentFontSize.get() == style){
            return;
        }
        this.currentFontSize.set(style);
        setFontSize(style);
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public ObservableBoolean getIsPdf() {
        return isPdf;
    }

    public void setIsPdf(boolean isPdf) {
        this.isPdf.set(isPdf);
    }

    public void onBackPDFClick() {
        eventBus.post(new ReaderSettingMenuItemBackPdfEvent());
    }

    public void onCustomizeItemClick() {
        eventBus.post(new ReaderSettingMenuItemCustomizeEvent());
    }

    public void onPresetItemClick(){
        settingInfo.settingType = ReaderConfig.SETTING_TYPE_PRESET;
        currentSettingType.set(settingInfo.settingType);
        ReaderSettingFontSizeEvent event = new ReaderSettingFontSizeEvent();
        event.styleIndex = settingInfo.settingStyle;
        event.settingType = currentSettingType.get();
        eventBus.post(event);
    }

    public void onTypefaceOneClick() {
        if (setCurrentTypeface(TypefaceOne)) {
            setTypeface(ReaderConfig.Typeface.TYPEFACE_ONE);
        }
    }

    public void onTypefaceTwoClick() {
        if (setCurrentTypeface(TypefaceTwo)) {
            setTypeface(ReaderConfig.Typeface.TYPEFACE_TWO);
        }
    }

    public void onTypefaceThreeClick() {
        if (setCurrentTypeface(TypefaceThree)) {
            setTypeface(ReaderConfig.Typeface.TYPEFACE_THREE);
        }
    }

    public void onTypefaceFourClick() {
        if (setCurrentTypeface(TypefaceFour)) {
            setTypeface(ReaderConfig.Typeface.TYPEFACE_FOUR);
        }
    }

    public void setTypeface(String typeface) {
        ReaderSettingTypefaceEvent event = new ReaderSettingTypefaceEvent();
        event.typeFace = typeface;
        event.styleIndex = currentFontSize.get();
        event.settingType = currentSettingType.get();
        if(settingInfo.settingType == ReaderConfig.SETTING_TYPE_CUSTOM){
            event.currentLineSpacing = ReaderConfig.customLineSpacing.get(settingInfo.customLineSpacing);
        }else {
            event.currentLineSpacing = ReaderConfig.presetStyle.get(currentFontSize.get()).getLineSpacing().getPercent();
        }

        eventBus.post(event);
    }

    public void onLevelOneClick() {
        setCurrentFontSize(ReaderConfig.SETTING_ONE_STYLE_KEY);
    }

    public void onLevelTwoClick() {
        setCurrentFontSize(ReaderConfig.SETTING_TWO_STYLE_KEY);
    }

    public void onLevelThreeClick() {
        setCurrentFontSize(ReaderConfig.SETTING_THREE_STYLE_KEY);
    }

    public void onLevelFourClick() {
        setCurrentFontSize(ReaderConfig.SETTING_FOUR_STYLE_KEY);
    }

    public void onLevelFiveClick() {
        setCurrentFontSize(ReaderConfig.SETTING_FIVE_STYLE_KEY);
    }

    public void onLevelSixClick() {
        setCurrentFontSize(ReaderConfig.SETTING_SIX_STYLE_KEY);
    }

    private void setFontSize(int style) {
        settingInfo.settingType = ReaderConfig.SETTING_TYPE_PRESET;
        currentSettingType.set(settingInfo.settingType);
        settingInfo.settingStyle = style;
        ReaderSettingFontSizeEvent event = new ReaderSettingFontSizeEvent();
        event.styleIndex = style;
        event.settingType = currentSettingType.get();
        eventBus.post(event);
    }

    public void onChangeChineseClick() {
        ChangeChineseConvertTypeEvent event = new ChangeChineseConvertTypeEvent();
        if (currentLanguage.get() == Language.Simplified) {
            event.convertType = ReaderChineseConvertType.SIMPLIFIED_TO_TRADITIONAL;
            setCurrentLanguage(Language.Traditional);
        } else {
            event.convertType = ReaderChineseConvertType.TRADITIONAL_TO_SIMPLIFIED;
            setCurrentLanguage(Language.Simplified);
        }

        eventBus.post(event);
    }
}
