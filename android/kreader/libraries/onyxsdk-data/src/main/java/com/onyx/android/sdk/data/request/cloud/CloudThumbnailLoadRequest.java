package com.onyx.android.sdk.data.request.cloud;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by suicheng on 2017/5/11.
 */

public class CloudThumbnailLoadRequest extends BaseCloudRequest {
    private static final String TAG = CloudThumbnailLoadRequest.class.getSimpleName();

    private String coverUrl;
    private String associationId;
    private OnyxThumbnail.ThumbnailKind thumbnailKind = OnyxThumbnail.ThumbnailKind.Large;
    private CloseableReference<Bitmap> refBitmap;

    public CloudThumbnailLoadRequest(final String coverUrl, final String associateId) {
        this.coverUrl = coverUrl;
        this.associationId = associateId;
    }

    public CloudThumbnailLoadRequest(final String coverUrl, final String associateId, OnyxThumbnail.ThumbnailKind thumbnailKind) {
        this(coverUrl, associateId);
        this.thumbnailKind = thumbnailKind;
    }

    public CloseableReference<Bitmap> getRefBitmap() {
        if (!isValid(refBitmap)) {
            return null;
        }
        return refBitmap.clone();
    }

    private boolean isValid(CloseableReference<Bitmap> bitmap) {
        if (bitmap != null && bitmap.isValid()) {
            return true;
        }
        return false;
    }

    private boolean isValid(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }
        return true;
    }

    @Override
    public void execute(final CloudManager cloudManager) throws Exception {
        if (StringUtils.isNullOrEmpty(associationId)) {
            return;
        }
        BitmapReferenceLruCache lruCache = cloudManager.getCacheManager().getBitmapLruCache();
        refBitmap = lruCache.get(associationId);
        if (isValid(refBitmap)) {
            return;
        }
        if (isAbort()) {
            return;
        }
        final File file = CloudUtils.imageCachePath(getContext(), associationId);
        if (file == null) {
            Log.w(TAG, "detect associationId is null");
            return;
        }
        if (file.exists() && file.length() > 0) {
            loadRefBitmap(lruCache, file);
            return;
        }
        if (!StringUtils.isUrl(coverUrl) || isAbort()) {
            return;
        }
        Bitmap myBitmap = loadBitmap(coverUrl);
        if (!isValid(myBitmap) || isAbort()) {
            return;
        }
        if (!writeBitmapToFile(file, myBitmap) || isAbort()) {
            return;
        }
        loadRefBitmap(lruCache, file);
    }

    private boolean writeBitmapToFile(File file, Bitmap originBitmap) throws Exception {
        Bitmap newBitmap = originBitmap;
        if (thumbnailKind != OnyxThumbnail.ThumbnailKind.Original) {
            newBitmap = ThumbnailUtils.createLargeThumbnail(originBitmap);
        }
        return ThumbnailUtils.writeBitmapToThumbnailFile(file, newBitmap);
    }

    private boolean loadRefBitmap(BitmapReferenceLruCache lruCache, File file) {
        boolean success = true;
        try {
            CloseableReference<Bitmap> bitmap = ThumbnailUtils.decodeFile(file);
            if (isValid(bitmap)) {
                lruCache.put(associationId, bitmap);
                refBitmap = bitmap.clone();
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private Bitmap loadBitmap(String url) throws Exception {
        if (thumbnailKind == OnyxThumbnail.ThumbnailKind.Original) {
            return loadOriginBitmap(url);
        } else {
            return loadLargeBitmap(url);
        }
    }

    private Bitmap loadLargeBitmap(String url) throws Exception {
        return Glide.with(getContext())
                .load(url)
                .asBitmap()
                .fitCenter()
                .into(512, 512)
                .get();
    }

    private Bitmap loadOriginBitmap(String url) throws Exception {
        return Glide.with(getContext())
                .load(url)
                .asBitmap()
                .into(ViewTarget.SIZE_ORIGINAL, ViewTarget.SIZE_ORIGINAL)
                .get();
    }

    private void saveToThumbnail(CloudManager cloudManager, File file, Bitmap bitmap) {
        boolean hasThumbnail = ThumbnailUtils.hasThumbnail(getContext(), cloudManager.getCloudDataProvider(), associationId);
        final DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        if (!hasThumbnail) {
            ThumbnailUtils.insertThumbnail(getContext(), cloudManager.getCloudDataProvider(),
                    file.getAbsolutePath(), associationId, thumbnailKind, bitmap);
        } else {
            ThumbnailUtils.updateThumbnailEntry(getContext(), cloudManager.getCloudDataProvider(), associationId,
                    thumbnailKind, bitmap);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public String getIdentifier() {
        return "cloudThumbnail";
    }
}
