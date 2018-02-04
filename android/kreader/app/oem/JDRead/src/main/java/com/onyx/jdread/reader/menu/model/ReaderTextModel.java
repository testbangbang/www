package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.event.ChangeChineseConvertTypeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingFontSizeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemBackPdfEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemCustomizeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingTypefaceEvent;

import org.greenrobot.eventbus.EventBus;

import static com.onyx.jdread.reader.menu.common.ReaderConfig.FontSize.FONT_SIZE_LARGE;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FontSize.FONT_SIZE_MEDIUM;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FontSize.FONT_SIZE_SMALL;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FontSize.FONT_SIZE_XX_LARGE;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FontSize.FONT_SIZE_X_LARGE;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FontSize.FONT_SIZE_X_SMALL;
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
    private ObservableField<ReaderFontSize> currentFontSize = new ObservableField<>(ReaderFontSize.LevelThreeFontSize);
    private ObservableField<Language> currentLanguage = new ObservableField<>(Language.Simplified);
    private EventBus eventBus;

    public ReaderTextModel(EventBus eventBus, ReaderTextStyle style, ReaderUserDataInfo readerUserDataInfo) {
        this.eventBus = eventBus;
        setDefaultStyle(style, readerUserDataInfo);
    }

    public void setDefaultStyle(ReaderTextStyle style, ReaderUserDataInfo readerUserDataInfo) {
        if(style != null) {
            setDefaultFontSize((int) style.getFontSize().getValue());
            ReaderChineseConvertType chineseConvertType = readerUserDataInfo.getChineseConvertType();
            setDefaultTypeFace(style.getFontFace());
            setDefaultLanguage(chineseConvertType);
        }
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
                onTypefaceOneClick();
                break;
            case ReaderConfig.Typeface.TYPEFACE_TWO:
                onTypefaceTwoClick();
                break;
            case ReaderConfig.Typeface.TYPEFACE_THREE:
                onTypefaceThreeClick();
                break;
            case ReaderConfig.Typeface.TYPEFACE_FOUR:
                onTypefaceFourClick();
                break;
            default:
                onTypefaceOneClick();
                break;
        }
    }

    public void setDefaultFontSize(int fontSize) {
        switch (fontSize) {
            case FONT_SIZE_X_SMALL:
                onLevelOneClick();
                break;
            case FONT_SIZE_SMALL:
                onLevelTwoClick();
                break;
            case FONT_SIZE_MEDIUM:
                onLevelThreeClick();
                break;
            case FONT_SIZE_LARGE:
                onLevelFourClick();
                break;
            case FONT_SIZE_X_LARGE:
                onLevelFiveClick();
                break;
            case FONT_SIZE_XX_LARGE:
                onLevelSixClick();
                break;
            default:
                onLevelThreeClick();
                break;
        }
    }

    public enum Language {
        Simplified, Traditional
    }

    public enum ReaderFontSize {
        LevelOneFontSize, LevelTwoFontSize, LevelThreeFontSize, LevelFourFontSize, LevelFiveFontSize, LevelSixFontSize
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

    public ObservableField<ReaderFontSize> getCurrentFontSize() {
        return currentFontSize;
    }

    public void setCurrentFontSize(ReaderFontSize fontSize) {
        this.currentFontSize.set(fontSize);
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
        eventBus.post(event);
    }

    public void onLevelOneClick() {
        setCurrentFontSize(ReaderFontSize.LevelOneFontSize);
        setFontSize(FONT_SIZE_X_SMALL);
    }

    public void onLevelTwoClick() {
        setCurrentFontSize(ReaderFontSize.LevelTwoFontSize);
        setFontSize(FONT_SIZE_SMALL);
    }

    public void onLevelThreeClick() {
        setCurrentFontSize(ReaderFontSize.LevelThreeFontSize);
        setFontSize(FONT_SIZE_MEDIUM);
    }

    public void onLevelFourClick() {
        setCurrentFontSize(ReaderFontSize.LevelFourFontSize);
        setFontSize(FONT_SIZE_LARGE);
    }

    public void onLevelFiveClick() {
        setCurrentFontSize(ReaderFontSize.LevelFiveFontSize);
        setFontSize(FONT_SIZE_X_LARGE);
    }

    public void onLevelSixClick() {
        setCurrentFontSize(ReaderFontSize.LevelSixFontSize);
        setFontSize(FONT_SIZE_XX_LARGE);
    }

    private void setFontSize(int fontSize) {
        ReaderSettingFontSizeEvent event = new ReaderSettingFontSizeEvent();
        event.fontSize = fontSize;
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
