package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.event.GammaCorrectionEvent;
import com.onyx.jdread.reader.menu.event.ImageReflowEvent;
import com.onyx.jdread.reader.menu.event.ResetNavigationEvent;
import com.onyx.jdread.reader.menu.event.ScaleToPageCropEvent;
import com.onyx.jdread.reader.menu.event.SwitchNavigationToComicModeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderImageModel {
    private ObservableBoolean isShow = new ObservableBoolean(false);
    private ObservableField<ImageShowMode> currentImageMode = new ObservableField<>(ImageShowMode.defaultMode);
    private ObservableInt currentFontColorDepth = new ObservableInt(ReaderConfig.TypefaceColorDepth.LEVEL_ONE);
    private ReaderDataHolder readerDataHolder;

    public ReaderImageModel(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public enum ImageShowMode {
        defaultMode, comicMode
    }

    public ObservableField<ImageShowMode> getCurrentImageMode() {
        return currentImageMode;
    }

    public void setCurrentImageMode(ImageShowMode imageMode) {
        this.currentImageMode.set(imageMode);
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public ObservableInt getCurrentFontColorDepth() {
        return currentFontColorDepth;
    }

    public void setCurrentFontColorDepth(int colorDepth) {
        this.currentFontColorDepth.set(colorDepth);
        setFontColorDepth(colorDepth);
    }

    public void onDefaultModeClick() {
        setCurrentImageMode(ImageShowMode.defaultMode);
        ResetNavigationEvent event = new ResetNavigationEvent();
        readerDataHolder.getEventBus().post(event);
    }

    public void onComicModeClick() {
        setCurrentImageMode(ImageShowMode.comicMode);
        SwitchNavigationToComicModeEvent showModeEvent = new SwitchNavigationToComicModeEvent();
        readerDataHolder.getEventBus().post(showModeEvent);
    }

    public void onOneFontColorDepthClick(){
        setCurrentFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_ONE);
    }

    public void onTwoFontColorDepthClick(){
        setCurrentFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_TWO);
    }

    public void onThreeFontColorDepthClick(){
        setCurrentFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_THREE);
    }

    public void onFourFontColorDepthClick(){
        setCurrentFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FOUR);
    }

    public void onFiveFontColorDepthClick(){
        setCurrentFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FIVE);
    }

    public void onSixFontColorDepthClick(){
        setCurrentFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_SIX);
    }

    private void setFontColorDepth(int textGamma){
        GammaCorrectionEvent event = new GammaCorrectionEvent();
        event.textGamma = textGamma;
        readerDataHolder.getEventBus().post(event);
    }

    public void onResetModeClick(){
        ResetNavigationEvent event = new ResetNavigationEvent();
        readerDataHolder.getEventBus().post(event);
    }

    public void onRearrangeClick(){
        ImageReflowEvent event = new ImageReflowEvent();
        readerDataHolder.getEventBus().post(event);
    }

    public void onTrimmingClick(){
        ScaleToPageCropEvent event = new ScaleToPageCropEvent();
        readerDataHolder.getEventBus().post(event);
    }
}
