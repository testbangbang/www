package com.onyx.android.sdk.reader.api;

/**
 * Created by zhuzeng on 10/7/15.
 */
public interface ReaderRendererFeatures {

    /**
     * Check if the document support scale or not.
     * @return true if supports.
     */
    public boolean supportScale();

    /**
     * support font size adjustment.
      * @return
     */
    public boolean supportFontSizeAdjustment();

    /**
     * support font gamma adjustment
     * @return
     */
    public boolean supportFontGammaAdjustment();

    /**
     * support type face adjustment.
     * @return
     */
    public boolean supportTypefaceAdjustment();

    /**
     * convert between simplified and traditional chinese text
     * @return
     */
    public boolean supportConvertBetweenSimplifiedAndTraditionalChineseText();

}
