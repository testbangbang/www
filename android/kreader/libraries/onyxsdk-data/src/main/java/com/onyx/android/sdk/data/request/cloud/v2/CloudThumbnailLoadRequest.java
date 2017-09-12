package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.FileUtils;
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
    private int reqWidth = ViewTarget.SIZE_ORIGINAL;
    private int reqHeight = ViewTarget.SIZE_ORIGINAL;

    public CloudThumbnailLoadRequest(final String coverUrl, final String associateId) {
        this.coverUrl = coverUrl;
        this.associationId = associateId;
    }

    public CloudThumbnailLoadRequest(final String coverUrl, final String associateId,
                                     OnyxThumbnail.ThumbnailKind thumbnailKind) {
        this(coverUrl, associateId);
        this.thumbnailKind = thumbnailKind;
    }

    public void setReqWidthHeight(int[] widthHeight) {
        this.reqWidth = widthHeight[0];
        this.reqHeight = widthHeight[1];
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
        if (loadFromMemoryCache(cloudManager) || isAbort()) {
            return;
        }
        if(loadFromCloud(cloudManager) || isAbort()) {
            return;
        }
        if (loadFromFileSystemCache(cloudManager) || isAbort()) {
            return;
        }
        loadFromDatabase(cloudManager);
    }

    private void saveToMemoryCache(final CloudManager cloudManager) {
        cloudManager.getCacheManager().getBitmapLruCache().put(getCacheKey(), refBitmap);
    }

    private File thumbnailFileSystemCachePathWidthId() {
        return CloudUtils.imageCachePath(getContext(), associationId, thumbnailKind.toString().toLowerCase());
    }

    private boolean loadFromMemoryCache(final CloudManager cloudManager) {
        BitmapReferenceLruCache lruCache = cloudManager.getCacheManager().getBitmapLruCache();
        refBitmap = lruCache.get(getCacheKey());
        if (isValid(refBitmap)) {
            return true;
        }
        return false;
    }

    private boolean loadFromFileSystemCache(final CloudManager cloudManager) {
        final File file = thumbnailFileSystemCachePathWidthId();
        if (file == null) {
            Log.w(TAG, "detect associationId is null");
            return false;
        }
        if (file.exists() && file.length() > 0 && loadRefBitmap(file)) {
            saveToMemoryCache(cloudManager);
            return true;
        }
        return false;
    }

    private boolean loadFromDatabase(final CloudManager cloudManager) {
        String path = loadThumbnailFilePathFromDatabase(getContext(), cloudManager.getCloudDataProvider(),
                associationId, thumbnailKind);
        if (StringUtils.isNullOrEmpty(path) || !FileUtils.fileExist(path)) {
            return false;
        }
        File file = new File(path);
        if (file.exists() && loadRefBitmap(file)) {
            saveToMemoryCache(cloudManager);
            return true;
        }
        return false;
    }

    private boolean loadFromCloud(final CloudManager cloudManager) {
        if (!StringUtils.isUrl(coverUrl) || isAbort()) {
            return false;
        }
        Bitmap myBitmap = loadBitmapFromCloudImpl(coverUrl);
        if (!isValid(myBitmap) || isAbort()) {
            return false;
        }
        final File file = thumbnailFileSystemCachePathWidthId();
        if (!writeBitmapToFile(file, myBitmap) || isAbort()) {
            return false;
        }
        if (loadRefBitmap(file)) {
            saveToMemoryCache(cloudManager);
        }
        return true;
    }

    private boolean writeBitmapToFile(File file, Bitmap originBitmap)  {
        return ThumbnailUtils.writeBitmapToThumbnailFile(file, originBitmap);
    }

    private boolean loadRefBitmap(File file) {
        boolean success = true;
        try {
            CloseableReference<Bitmap> bitmap = ThumbnailUtils.decodeFile(file);
            if (isValid(bitmap)) {
                refBitmap = bitmap.clone();
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private Bitmap loadBitmapFromCloudImpl(String url) {
        return loadOriginBitmapFromCloudImpl(url);
    }

    private Bitmap loadLargeBitmapFromCloudImpl(String url)  {
        try {
            return Glide.with(getContext())
                    .load(url)
                    .asBitmap()
                    .fitCenter()
                    .into(512, 512)
                    .get();
        } catch (Exception e) {
            return null;
        }
    }

    private Bitmap loadOriginBitmapFromCloudImpl(String url) {
        try {
            return Glide.with(getContext())
                    .load(url)
                    .asBitmap()
                    .into(reqWidth, reqHeight)
                    .get();
        } catch (Exception e) {
            return null;
        }
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

    private String loadThumbnailFilePathFromDatabase(Context context,
                                                     DataProviderBase dataProvider,
                                                     String associationId,
                                                     OnyxThumbnail.ThumbnailKind thumbnailKind) {
        Thumbnail thumbnail = dataProvider.getThumbnailEntry(context, associationId, thumbnailKind);
        if (thumbnail == null) {
            return null;
        }
        return thumbnail.getImageDataPath();
    }

    private String getCacheKey() {
        return CacheManager.generateCloudThumbnailKey(associationId, coverUrl, thumbnailKind.toString().toLowerCase());
    }
}
