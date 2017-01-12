package com.onyx.android.sdk.reader.reflow;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by joy on 10/13/16.
 */
public class ReflowTask extends BaseRequest {
    private ImageReflowManager manager;
    private String pageName;
    private Bitmap bitmap;

    public ReflowTask(ImageReflowManager manager, String pageName, Bitmap bitmap) {
        this.manager = manager;
        this.pageName = pageName;
        this.bitmap = bitmap;
    }

    public void execute() {
        if (isAbort()) {
            return;
        }
        manager.reflowBitmap(pageName, bitmap);
    }

    public String getPageName() {
        return pageName;
    }
}
