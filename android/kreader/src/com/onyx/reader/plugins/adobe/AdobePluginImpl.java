package com.onyx.reader.plugins.adobe;

import android.graphics.Bitmap;
import com.onyx.reader.api.*;

import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobePluginImpl {

    /* load our native library */
    static {
        System.loadLibrary("onyx_pdf");
    }
    private ReaderDocument document = null;
    private String absoluteLocalDocumentPath;

    static public int PM_HARD_PAGES = 0;
    static public int PM_FLOW_PAGES = 2;
    static public int PM_SCROLL_PAGES = 3;

    public native long openFile(String filename, String password, String zipPassword);
    public native void closeFile();
    public native int countPagesInternal();
    public native boolean gotoLocationInternal(int localActionPageNum, String internalLocation);
    public native void pageSizeNative(int page, float [] size);
    private native float getPageWidth();
    private native float getPageHeight();
    public native long drawPage(int page,
                                 Bitmap bitmap,
                                 int displayLeft, int displayTop,
                                 int displayWidth, int displayHeight, boolean fill);

    public native boolean drawVisiblePages(Bitmap bitmap,
                                            int displayLeft, int displayTop,
                                            int displayWidth, int displayHeight, boolean fill);

    private native int getPageNumberOfScreenPoint(double screenX, double screenY);
    private native double[] convertPointFromDeviceSpaceToDocumentSpace(double screenX, double screenY, int pageNum);
    private native double[] convertPointFromDocumentSpaceToDeviceSpace(double docX, double docY, int pageNum);

    private native boolean setPageMode(int m);
    private native String getTextNative(String start, String end);
    private native String getPageTextNative(int pageNumber);
    private native boolean getMetadataNative(ReaderDocumentMetadata object, final List<String> tagList);
    private native boolean nextScreenNative();
    private native boolean prevScreenNative();
    private native boolean setFontSizeNative(double width, double height, double size);
    private native int collectVisibleLinksNative(List<ReaderLink> list);
    private native int getPageNumberByLocationNative(String location);

    public native void setAbortFlagNative(boolean abort);
    public native boolean getAbortFlagNative();
//    private native ReaderTextSelection hitTestNative(float x, float y, int type, ReaderTextSplitter splitter);
    private native double [] rectangles(String start, String end);
    private native double [] pageDisplayRectangles(int page, int count);
//    private native int allVisiblePagesRectangle(List<ReaderPageInfo> list);
    private native double [] updateLocationNative();
    private native boolean changeNavigationMatrix(double scale, double dx, double dy);
    public native boolean setNavigationMatrix(double scale, double absX, double absY);

//    private native boolean searchNextNative(String pattern, boolean caseSensitive, boolean matchWholeWord, int start, int end, List<ReaderLocationRange> list);
//    private native boolean searchPrevNative(String pattern, boolean caseSensitive, boolean matchWholeWord, int start, int end, List<ReaderLocationRange> list);
//    private native boolean searchAllInPageNative(String pattern, boolean caseSensitive, boolean matchWholeWord, int start, int end, List<ReaderLocationRange> list);
    private native int getPageOrientationNative(int page);


    private native boolean hasTableOfContent();
    private native boolean getTableOfContent(ReaderDocumentTableOfContentEntry root);

    private native boolean isLocationInCurrentScreenNative(String internalLocation);

    /**
     * accept font face path as "fonts/xxx.ttf(otf)" which located in adobe resource folder,
     * null means to use default font
     *
     * @param fontResPath
     * @return
     */
    private native boolean setFontFaceInternal(String fontResPath);
    private native boolean setDisplayMarginsNative(double left, double top, double right, double bottom);

    //----------------------------------------------------------------------------------
    // DRM fulfill related interfaces
    //----------------------------------------------------------------------------------

    public static native void initDeviceForDRM(String deviceName, String deviceSerial, String applicationPrivateStorage);

    /**
     * Register the callback only for DRM related functions
     * @param callback
     * @return
     */
    public static native boolean registerAdobeDRMCallback(ReaderDRMCallback callback);

    /**
     * Fulfill the DRM content by *.acsm file
     * @param path the file path of the *.acsm file
     * @return
     */
    public static native boolean fulfillByAcsm(String path);

    /**
     * Activate device with adobe ID and password
     * @param adobeId is the adobe ID that registered in adobe site
     * @param password the password for this adobe ID
     * @return
     */
    public static native boolean activateDevice(String adobeId, String password);

    /**
     * Get the activated DRM account by using C++ backend. The DRM account could be
     * activated by:
     * <li>ADE through USB connection from PC</li>
     * <li>OTA through wifi</li>
     * @return the DRM account name (adobe id)
     */
    public static native String getActivatedDRMAccount();

    /**
     * Deactivate the device with DRM accessibility
     * @return true if successfully done, false otherwise
     */
    public static native boolean deactivateDevice();

    //public static native ReaderSentenceResult getNextSentence(final ReaderTextSplitter splitter, String internalBeginLocation);

}
