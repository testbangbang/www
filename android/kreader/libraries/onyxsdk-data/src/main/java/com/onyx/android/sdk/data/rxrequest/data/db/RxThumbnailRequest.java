package com.onyx.android.sdk.data.rxrequest.data.db;

import android.graphics.Bitmap;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;

/**
 * Created by suicheng on 2016/9/7.
 */
public class RxThumbnailRequest extends RxBaseDBRequest {
    private ThumbnailKind thumbnailKind = ThumbnailKind.Middle;
    private String path;
    private String sourceMD5;
    private Thumbnail thumbnail;
    private Bitmap resultBitmap;

    public RxThumbnailRequest(DataManager dataManager,String md5, String path) {
        super(dataManager);
        this.sourceMD5 = md5;
        this.path = path;
    }

    public RxThumbnailRequest(DataManager dataManager,String md5, String path, ThumbnailKind kind) {
        this(dataManager,md5, path);
        this.thumbnailKind = kind;
    }

    @Override
    public RxThumbnailRequest call() throws Exception {
        thumbnail = DataManagerHelper.loadThumbnail(getAppContext(), path, sourceMD5, thumbnailKind);
        resultBitmap = DataManagerHelper.loadThumbnailBitmap(getAppContext(), thumbnail);
        return this;
    }

    public Bitmap getResultBitmap() {
        return resultBitmap;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }
}
