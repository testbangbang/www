package com.onyx.android.sdk.data.request.data;

import android.graphics.Bitmap;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Thumbnail;

/**
 * Created by suicheng on 2016/9/7.
 */
public class ThumbnailRequest extends BaseDataRequest {
    private ThumbnailKind thumbnailKind = ThumbnailKind.Middle;
    private String sourceMD5;
    private Bitmap resultBitmap;

    public ThumbnailRequest(String md5) {
        this.sourceMD5 = md5;
    }

    public ThumbnailRequest(String md5, ThumbnailKind kind) {
        this.sourceMD5 = md5;
        this.thumbnailKind = kind;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        Thumbnail thumbnail = getDataProviderBase(dataManager).loadThumbnail(getContext(), sourceMD5, this.thumbnailKind);
        if (thumbnail == null) {
            return;
        }
        resultBitmap = getDataProviderBase(dataManager).loadThumbnailBitmap(getContext(), thumbnail);
    }

    public Bitmap getResultBitmap() {
        return resultBitmap;
    }
}
