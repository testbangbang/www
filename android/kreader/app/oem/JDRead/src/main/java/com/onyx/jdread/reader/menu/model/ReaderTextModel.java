package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemBackPdfEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemCustomizeEvent;

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
    private ObservableField<ReaderFontSize> currentFontSize = new ObservableField<>(ReaderFontSize.LevelOneFontSize);

    public enum ReaderFontSize{
        LevelOneFontSize,LevelTwoFontSize,LevelThreeFontSize,LevelFourFontSize,LevelFiveFontSize,LevelSixFontSize
    }

    public enum ReaderTypeface {
        boldFaceType, arialTypeface, italicsTypeface, roundBodyTypeface
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
    }

    public void onArialTypefaceClick() {
        setCurrentTypeface(arialTypeface);
    }

    public void onItalicsTypefaceClick() {
        setCurrentTypeface(italicsTypeface);
    }

    public void onRoundBodyTypefaceClick() {
        setCurrentTypeface(roundBodyTypeface);
    }

    public void onLevelOneClick(){
        setCurrentFontSize(ReaderFontSize.LevelOneFontSize);
    }

    public void onLevelTwoClick(){
        setCurrentFontSize(ReaderFontSize.LevelTwoFontSize);
    }

    public void onLevelThreeClick(){
        setCurrentFontSize(ReaderFontSize.LevelThreeFontSize);
    }

    public void onLevelFourClick(){
        setCurrentFontSize(ReaderFontSize.LevelFourFontSize);
    }

    public void onLevelFiveClick(){
        setCurrentFontSize(ReaderFontSize.LevelFiveFontSize);
    }

    public void onLevelSixClick(){
        setCurrentFontSize(ReaderFontSize.LevelSixFontSize);
    }
}
