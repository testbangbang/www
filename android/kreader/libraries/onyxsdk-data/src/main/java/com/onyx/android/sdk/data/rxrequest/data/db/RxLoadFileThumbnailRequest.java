package com.onyx.android.sdk.data.rxrequest.data.db;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;

/**
 * Created by jackdeng on 2017/12/1.
 */

public class RxLoadFileThumbnailRequest extends RxBaseDBRequest {

    private ThumbnailKind thumbnailKind = ThumbnailKind.Middle;
    private String originContentPath;
    private CloseableReference<Bitmap> refBitmap;

    public RxLoadFileThumbnailRequest(DataManager dataManager, String originContentPath) {
        super(dataManager);
        this.originContentPath = originContentPath;
    }

    public RxLoadFileThumbnailRequest(DataManager dataManager, String originContentPath, ThumbnailKind kind) {
        this(dataManager, originContentPath);
        this.thumbnailKind = kind;
    }

    @Override
    public RxLoadFileThumbnailRequest call() throws Exception {
        refBitmap = DataManagerHelper.loadThumbnailBitmapWithCacheByOriginContentPath(getAppContext(), getDataManager(), originContentPath);
        return this;
    }

    public CloseableReference<Bitmap> getResultRefBitmap() {
        return refBitmap;
    }
}