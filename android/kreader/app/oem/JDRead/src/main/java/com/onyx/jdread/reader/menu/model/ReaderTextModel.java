package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.reader.menu.actions.SettingFontSizeAction;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.event.ChangeChineseConvertTypeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingFontSizeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemBackPdfEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemCustomizeEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingTypefaceEvent;

import org.greenrobot.eventbus.EventBus;

import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.arialTypeface;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.boldFaceType;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.italicsTypeface;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.roundBodyTypeface;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderTextModel {
    private ObservableBoolean isShow = new ObservableBoolean(false);
    private ObservableBoolean isPdf = new ObservableBoolean(false);
    private ObservableField<ReaderTypeface> currentTypeface = new ObservableField<>(boldFaceType);
    private ObservableField<ReaderFontSize> currentFontSize = new ObservableField<>(ReaderFontSize.LevelThreeFontSize);
    private ObservableField<Language> currentLanguage = new ObservableField<>(Language.Simplified);
    public enum Language{
        Simplified,Traditional
    }

    public enum ReaderFontSize {
        LevelOneFontSize, LevelTwoFontSize, LevelThreeFontSize, LevelFourFontSize, LevelFiveFontSize, LevelSixFontSize
    }

    public enum ReaderTypeface {
        boldFaceType, arialTypeface, italicsTypeface, roundBodyTypeface
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

    public void setCurrentTypeface(ReaderTypeface typeface) {
        this.currentTypeface.set(typeface);
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
        EventBus.getDefault().post(new ReaderSettingMenuItemBackPdfEvent());
    }

    public void onCustomizeItemClick() {
        EventBus.getDefault().post(new ReaderSettingMenuItemCustomizeEvent());
    }

    public void onBoldfaceTypefaceClick() {
        setCurrentTypeface(boldFaceType);
        setTypeface(ReaderConfig.Typeface.BOLD_FACE_TYPEFACE);
    }

    public void onArialTypefaceClick() {
        setCurrentTypeface(arialTypeface);
        setTypeface(ReaderConfig.Typeface.ARIAL_TYPEFACE);
    }

    public void onItalicsTypefaceClick() {
        setCurrentTypeface(italicsTypeface);
        setTypeface(ReaderConfig.Typeface.ITALICS_TYPEFACE);
    }

    public void onRoundBodyTypefaceClick() {
        setCurrentTypeface(roundBodyTypeface);
        setTypeface(ReaderConfig.Typeface.ROUND_BODY_TYPEFACE);
    }

    public void setTypeface(String typeface){
        ReaderSettingTypefaceEvent event = new ReaderSettingTypefaceEvent();
        event.typeFace = typeface;
        EventBus.getDefault().post(event);
    }

    public void onLevelOneClick() {
        setCurrentFontSize(ReaderFontSize.LevelOneFontSize);
        setFontSize(ReaderConfig.FontSize.FONT_SIZE_X_SMALL);
    }

    public void onLevelTwoClick() {
        setCurrentFontSize(ReaderFontSize.LevelTwoFontSize);
        setFontSize(ReaderConfig.FontSize.FONT_SIZE_SMALL);
    }

    public void onLevelThreeClick() {
        setCurrentFontSize(ReaderFontSize.LevelThreeFontSize);
        setFontSize(ReaderConfig.FontSize.FONT_SIZE_MEDIUM);
    }

    public void onLevelFourClick() {
        setCurrentFontSize(ReaderFontSize.LevelFourFontSize);
        setFontSize(ReaderConfig.FontSize.FONT_SIZE_LARGE);
    }

    public void onLevelFiveClick() {
        setCurrentFontSize(ReaderFontSize.LevelFiveFontSize);
        setFontSize(ReaderConfig.FontSize.FONT_SIZE_X_LARGE);
    }

    public void onLevelSixClick() {
        setCurrentFontSize(ReaderFontSize.LevelSixFontSize);
        setFontSize(ReaderConfig.FontSize.FONT_SIZE_XX_LARGE);
    }

    private void setFontSize(int fontSize){
        ReaderSettingFontSizeEvent event = new ReaderSettingFontSizeEvent();
        event.fontSize = fontSize;
        EventBus.getDefault().post(event);
    }

    public void onChangeChineseClick(){
        ChangeChineseConvertTypeEvent event = new ChangeChineseConvertTypeEvent();
        if(currentLanguage.get() == Language.Simplified){
            event.convertType = ReaderChineseConvertType.SIMPLIFIED_TO_TRADITIONAL;
            setCurrentLanguage(Language.Traditional);
        }else{
            event.convertType = ReaderChineseConvertType.TRADITIONAL_TO_SIMPLIFIED;
            setCurrentLanguage(Language.Simplified);
        }

        EventBus.getDefault().post(event);
    }
}
