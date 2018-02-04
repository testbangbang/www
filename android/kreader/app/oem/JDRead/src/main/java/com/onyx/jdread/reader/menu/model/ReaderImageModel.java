package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.jdread.reader.common.GammaInfo;
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
    private EventBus eventBus;

    public ReaderImageModel(EventBus eventBus, GammaInfo gammaInfo) {
        this.eventBus = eventBus;
        setDefaultTypeColorDepth(gammaInfo);
    }

    private void setDefaultTypeColorDepth(GammaInfo gammaInfo) {
        switch (gammaInfo.getEmboldenLevel()) {
            case ReaderConfig.TypefaceColorDepth.LEVEL_ONE:
                updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_ONE);
                break;
            case ReaderConfig.TypefaceColorDepth.LEVEL_TWO:
                updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_TWO);
                break;
            case ReaderConfig.TypefaceColorDepth.LEVEL_THREE:
                updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_THREE);
                break;
            case ReaderConfig.TypefaceColorDepth.LEVEL_FOUR:
                updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FOUR);
                break;
            case ReaderConfig.TypefaceColorDepth.LEVEL_FIVE:
                updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FIVE);
                break;
            case ReaderConfig.TypefaceColorDepth.LEVEL_SIX:
                updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_SIX);
                break;
            default:
                updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_ONE);
                break;
        }
    }

    public EventBus getEventBus() {
        return eventBus;
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

    private boolean updateImageMode(ImageShowMode imageShowMode) {
        if (currentImageMode.get() == imageShowMode) {
            return false;
        }
        setCurrentImageMode(imageShowMode);
        return true;
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
        currentFontColorDepth.set(colorDepth);
    }

    private boolean updateFontColorDepth(int colorDepth) {
        if (currentFontColorDepth.get() == colorDepth) {
            return false;
        }
        setCurrentFontColorDepth(colorDepth);
        return true;
    }

    public void onDefaultModeClick() {
        if (updateImageMode(ImageShowMode.defaultMode)) {
            ResetNavigationEvent event = new ResetNavigationEvent();
            getEventBus().post(event);
        }
    }

    public void onComicModeClick() {
        if (updateImageMode(ImageShowMode.comicMode)) {
            SwitchNavigationToComicModeEvent showModeEvent = new SwitchNavigationToComicModeEvent();
            getEventBus().post(showModeEvent);
        }
    }

    public void onOneFontColorDepthClick() {
        if (updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_ONE)) {
            setFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_ONE);
        }
    }

    public void onTwoFontColorDepthClick() {
        if (updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_TWO)) {
            setFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_TWO);
        }
    }

    public void onThreeFontColorDepthClick() {
        if (updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_THREE)) {
            setFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_THREE);
        }
    }

    public void onFourFontColorDepthClick() {
        if (updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FOUR)) {
            setFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FOUR);
        }
    }

    public void onFiveFontColorDepthClick() {
        if (updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FIVE)) {
            setFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_FIVE);
        }
    }

    public void onSixFontColorDepthClick() {
        if (updateFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_SIX)) {
            setFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_SIX);
        }
    }

    private void setFontColorDepth(int textGamma) {
        GammaCorrectionEvent event = new GammaCorrectionEvent();
        event.textGamma = textGamma;
        getEventBus().post(event);
    }

    public void onResetModeClick() {
        resetMode();
        ResetNavigationEvent event = new ResetNavigationEvent();
        getEventBus().post(event);
    }

    public void resetMode() {
        setCurrentImageMode(ImageShowMode.defaultMode);
        setCurrentFontColorDepth(ReaderConfig.TypefaceColorDepth.LEVEL_ONE);
    }

    public void onRearrangeClick() {
        ImageReflowEvent event = new ImageReflowEvent();
        getEventBus().post(event);
    }

    public void onTrimmingClick() {
        ScaleToPageCropEvent event = new ScaleToPageCropEvent();
        getEventBus().post(event);
    }
}
