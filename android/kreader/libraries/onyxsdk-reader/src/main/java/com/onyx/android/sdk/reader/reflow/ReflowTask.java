package com.onyx.android.sdk.reader.reflow;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;

/**
 * Created by joy on 10/13/16.
 */
public class ReflowTask extends BaseRequest {
    private ImageReflowManager manager;
    private String pageName;
    private ReaderBitmapReferenceImpl bitmap;

    public ReflowTask(ImageReflowManager manager, String pageName, ReaderBitmapReferenceImpl bitmap) {
        this.manager = manager;
        this.pageName = pageName;
        this.bitmap = bitmap;
    }

    public void execute() {
        if (isAbort()) {
            return;
        }
        manager.reflowBitmap(pageName, bitmap.getBitmap());
        closeBitmap();
    }

    @Override
    public void setAbort() {
        super.setAbort();
        closeBitmap();
    }

    public String getPageName() {
        return pageName;
    }

    private void closeBitmap() {
        if (bitmap != null) {
            bitmap.close();
            bitmap = null;
        }
    }
}
