package com.onyx.jdread.reader.common;

import android.content.Context;
import android.view.SurfaceView;

import com.onyx.jdread.R;

/**
 * Created by huxiaomao on 2018/1/24.
 */

public class ReaderViewConfig {
    private static float readerBottomStateBarHeight;

    public static void setReaderBottomStateBarHeight(float readerBottomStateBarHeight) {
        ReaderViewConfig.readerBottomStateBarHeight = readerBottomStateBarHeight;
    }

    public static float getReaderBottomStateBarHeight() {
        return readerBottomStateBarHeight;
    }

    public static int getContentWidth(SurfaceView contentView) {
        return contentView.getWidth();
    }

    public static int getContentHeight(SurfaceView contentView) {
        int height = (int)(contentView.getHeight() - readerBottomStateBarHeight);
        return height;
    }
}
