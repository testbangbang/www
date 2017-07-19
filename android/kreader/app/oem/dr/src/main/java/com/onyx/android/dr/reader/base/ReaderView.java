package com.onyx.android.dr.reader.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by huxiaomao on 17/5/4.
 */

public interface ReaderView {
    void updatePage(final Bitmap bitmap);
    Context getViewContext() ;
    View getView();
    Context getApplicationContext();
    void showThrowable(Throwable throwable);
}
