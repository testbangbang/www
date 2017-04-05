package com.onyx.android.sdk.data.request.data.db;

import android.graphics.Bitmap;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Thumbnail;

/**
 * Created by suicheng on 2016/9/7.
 */
public class ThumbnailRequest extends BaseDbRequest {
    private ThumbnailKind thumbnailKind = ThumbnailKind.Middle;
    private String path;
    private String sourceMD5;
    private Thumbnail thumbnail;
    private Bitmap resultBitmap;

    public ThumbnailRequest(String md5, String path) {
        this.sourceMD5 = md5;
        this.path = path;
    }

    public ThumbnailRequest(String md5, String path, ThumbnailKind kind) {
        this(md5, path);
        this.thumbnailKind = kind;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        thumbnail = DataManagerHelper.loadThumbnail(getContext(), path, sourceMD5, thumbnailKind);
        resultBitmap = DataManagerHelper.loadThumbnailBitmap(getContext(), thumbnail);
    }

    public Bitmap getResultBitmap() {
        return resultBitmap;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }
}
