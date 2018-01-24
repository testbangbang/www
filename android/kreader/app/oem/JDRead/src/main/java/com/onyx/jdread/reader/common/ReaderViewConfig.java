package com.onyx.jdread.reader.common;

import android.content.Context;
import android.view.SurfaceView;

import com.onyx.jdread.R;

/**
 * Created by huxiaomao on 2018/1/24.
 */

public class ReaderViewConfig {
    public static int getContentWidth(Context context,SurfaceView contentView) {
        return contentView.getWidth();
    }

    public static int getContentHeight(Context context,SurfaceView contentView) {
        final float readerBottomStateBarHeight = context.getResources().getDimension(R.dimen.reader_content_view_bottom_state_bar_height);
        int height = (int)(contentView.getHeight() - readerBottomStateBarHeight);
        return height;
    }
}
