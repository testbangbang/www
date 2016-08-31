package com.onyx.android.sdk.data.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.onyx.android.sdk.data.CloudManager;

/**
 * Created by zhuzeng on 11/23/15.
 */
public class ParseCoverRequest extends BaseCloudRequest {
    static final String TAG = ParseCoverRequest.class.getSimpleName();
    private String path;
    private volatile Bitmap bitmap;

    public ParseCoverRequest(final String path) {
        this.path = path;
    }

    public final Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        bitmap = BitmapFactory.decodeFile(path);
    }
}
