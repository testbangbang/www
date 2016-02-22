package com.onyx.kreader.host.options;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class ReaderConstants {

    /**
     * layout section.
     */
    public static final String SINGLE_PAGE = "singlePage";
    public static final String SINGLE_PAGE_NAVIGATION_LIST = "singlePageNavigationList";
    public static final String CONTINUOUS_PAGE = "continuousPage";
    public static final String REFLOW_PAGE = "reflowPage";
    public static final String SCANNED_REFLOW_PAGE = "scanReflowPage";

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
        if (scale <= SCALE_INVALID && scale >= SCALE_TO_WIDTH_CONTENT) {
            return true;
        }
        return false;
    }


    static public int DEFAULT_GAMMA = 150;

    static public double DEFAULT_AUTO_CROP_VALUE = 0.01;

    static public int DEFAULT_PARAGRAPH_INDENT = 2;

    static public int DEFAULT_LINE_SPACING = 110;



}
