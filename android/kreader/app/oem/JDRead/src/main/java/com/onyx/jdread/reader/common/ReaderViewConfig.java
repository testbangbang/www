package com.onyx.jdread.reader.common;

import android.content.Context;
import android.graphics.PointF;
import android.view.SurfaceView;

import com.onyx.jdread.R;

/**
 * Created by huxiaomao on 2018/1/24.
 */

public class ReaderViewConfig {
    private static float readerBottomStateBarHeight;
    private static float timeFontSize;
    private static float timeMarginLeft;
    private static float timeMarginBottom;
    private static float pageNumberFontSize;
    private static float pageNumberMarginRight;
    private static float pageNumberMarginBottom;

    public static void setReaderBottomStateBarHeight(float readerBottomStateBarHeight) {
        ReaderViewConfig.readerBottomStateBarHeight = readerBottomStateBarHeight;
    }

    public static void setTimeFontSize(float fontSize) {
        ReaderViewConfig.timeFontSize = fontSize;
    }

    public static float getTimeFontSize() {
        return timeFontSize;
    }

    public static float getTimeMarginLeft() {
        return timeMarginLeft;
    }

    public static float getPageNumberFontSize() {
        return pageNumberFontSize;
    }

    public static float getPageNumberMarginRight() {
        return pageNumberMarginRight;
    }

    public static void setPageNumberMarginRight(float pageNumberMarginRight) {
        ReaderViewConfig.pageNumberMarginRight = pageNumberMarginRight;
    }

    public static void setTimeMarginLeft(float timeMarginLeft) {
        ReaderViewConfig.timeMarginLeft = timeMarginLeft;
    }

    public static void setPageNumberFontSize(float fontSize) {
        ReaderViewConfig.pageNumberFontSize = fontSize;
    }

    public static float getTimeMarginBottom() {
        return timeMarginBottom;
    }

    public static void setTimeMarginBottom(float timeMarginBottom) {
        ReaderViewConfig.timeMarginBottom = timeMarginBottom;
    }

    public static float getPageNumberMarginBottom() {
        return pageNumberMarginBottom;
    }

    public static void setPageNumberMarginBottom(float pageNumberMarginBottom) {
        ReaderViewConfig.pageNumberMarginBottom = pageNumberMarginBottom;
    }

    public static float getReaderBottomStateBarHeight() {
        return readerBottomStateBarHeight;
    }

    public static int getContentWidth(SurfaceView contentView) {
        return contentView.getWidth();
    }

    public static int getContentHeight(SurfaceView contentView) {
        int height = (int) (contentView.getHeight() - readerBottomStateBarHeight);
        return height;
    }

    public static PointF getTimePoint(SurfaceView contentView) {
        PointF point = new PointF();
        point.x = getTimeMarginLeft();
        point.y = contentView.getHeight() - getTimeMarginBottom();
        return point;
    }

    public static PointF getPageNumberPoint(SurfaceView contentView) {
        PointF point = new PointF();
        point.x = getContentWidth(contentView) - getPageNumberMarginRight();
        point.y = contentView.getHeight() - getPageNumberMarginBottom();
        return point;
    }
}
