package com.onyx.jdread.reader.common;

import com.onyx.android.sdk.reader.host.options.BaseOptions;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class GammaInfo {
    private int globalGamma = BaseOptions.getGlobalDefaultGamma();
    private int imageGamma = BaseOptions.getGlobalDefaultGamma();
    private int textGamma = BaseOptions.getGlobalDefaultGamma();
    private int emboldenLevel = 0;

    public int getGlobalGamma() {
        return globalGamma;
    }

    public void setGlobalGamma(int globalGamma) {
        this.globalGamma = globalGamma;
    }

    public int getImageGamma() {
        return imageGamma;
    }

    public void setImageGamma(int imageGamma) {
        this.imageGamma = imageGamma;
    }

    public int getTextGamma() {
        return textGamma;
    }

    public void setTextGamma(int textGamma) {
        this.textGamma = textGamma;
    }

    public int getEmboldenLevel() {
        return emboldenLevel;
    }

    public void setEmboldenLevel(int emboldenLevel) {
        this.emboldenLevel = emboldenLevel;
    }
}
