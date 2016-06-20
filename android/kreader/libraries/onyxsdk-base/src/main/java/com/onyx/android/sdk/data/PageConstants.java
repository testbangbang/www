package com.onyx.android.sdk.data;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class PageConstants {

    /**
     * layout section.
     */
    public static final String SINGLE_PAGE = "singlePage";
    public static final String SINGLE_PAGE_NAVIGATION_LIST = "singlePageNavigationList";
    public static final String CONTINUOUS_PAGE = "continuousPage";
    public static final String IMAGE_REFLOW_PAGE = "imageReflowPage";
    public static final String TEXT_REFLOW_PAGE = "textReflowPage";

    /**
     * scale
     */
    public static final int SCALE_INVALID = 0;
    public static final int SCALE_TO_PAGE = -1;
    public static final int SCALE_TO_WIDTH = -2;
    public static final int SCALE_TO_HEIGHT = -3;
    public static final int SCALE_TO_PAGE_CONTENT = -4;
    public static final int SCALE_TO_WIDTH_CONTENT = -5;
    public static final int ZOOM_TO_SCAN_REFLOW = -6;
    public static final int ZOOM_TO_REFLOW = -7;
    public static final int ZOOM_TO_COMICE = -8;
    public static final int ZOOM_TO_PAPER = -9;

    public static boolean isSpecialScale(int scale) {
        if (scale < SCALE_INVALID && scale >= SCALE_TO_WIDTH_CONTENT) {
            return true;
        }
        return false;
    }

    public static boolean isScaleToPage(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_PAGE;
    }

    public static boolean isScaleToWidth(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_WIDTH;
    }

    public static boolean isScaleToHeight(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_HEIGHT;
    }

    public static boolean isScaleToPageContent(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_PAGE_CONTENT;
    }

    public static boolean isWidthCrop(int specialScale) {
        return specialScale == PageConstants.SCALE_TO_WIDTH_CONTENT;
    }



    static public int GAMMA_LOWER_LIMIT = 100;
    static public int DEFAULT_GAMMA = 100;

    static public double DEFAULT_AUTO_CROP_VALUE = 0.01;

    static public int DEFAULT_PARAGRAPH_INDENT = 2;

    static public int DEFAULT_LINE_SPACING = 110;



}
