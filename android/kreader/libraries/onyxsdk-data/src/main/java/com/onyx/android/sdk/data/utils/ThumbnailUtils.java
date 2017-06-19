package com.onyx.android.sdk.data.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.PoolConfig;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by suicheng on 2016/9/5.
 */
public class ThumbnailUtils {
    public static final String thumbnail_folder = ".thumbnails";
    public static final String preferred_extension = "png";

    static private Map<String, Integer> defaultThumbnailMap = new HashMap<>();

    static public Map<String, Integer> defaultThumbnailMapping() {
        if (defaultThumbnailMap == null) {
            defaultThumbnailMap = new HashMap<>();
            defaultThumbnailMap.put("acsm", R.drawable.acsm);
            defaultThumbnailMap.put("bmp", R.drawable.bmp);
            defaultThumbnailMap.put("chm", R.drawable.chm);
            defaultThumbnailMap.put("djvu", R.drawable.djvu);
            defaultThumbnailMap.put("doc", R.drawable.doc);
            defaultThumbnailMap.put("docx", R.drawable.docx);
            defaultThumbnailMap.put("eba3", R.drawable.eba3);
            defaultThumbnailMap.put("ebaml", R.drawable.ebaml);
            defaultThumbnailMap.put("epub", R.drawable.epub);
            defaultThumbnailMap.put("fb2", R.drawable.fb2);
            defaultThumbnailMap.put("gif", R.drawable.gif);
            defaultThumbnailMap.put("htm", R.drawable.htm);
            defaultThumbnailMap.put("html", R.drawable.html);
            defaultThumbnailMap.put("jpg", R.drawable.jpg);
            defaultThumbnailMap.put("mobi", R.drawable.mobi);
            defaultThumbnailMap.put("mp3", R.drawable.mp3);
            defaultThumbnailMap.put("pdb", R.drawable.pdb);
            defaultThumbnailMap.put("pdf", R.drawable.pdf);
            defaultThumbnailMap.put("png", R.drawable.png);
            defaultThumbnailMap.put("ppt", R.drawable.ppt);
            defaultThumbnailMap.put("prc", R.drawable.prc);
            defaultThumbnailMap.put("rar", R.drawable.rar);
            defaultThumbnailMap.put("rtf", R.drawable.rtf);
            defaultThumbnailMap.put("tiff", R.drawable.tiff);
            defaultThumbnailMap.put("txt", R.drawable.txt);
            defaultThumbnailMap.put("wma", R.drawable.wma);
            defaultThumbnailMap.put("xls", R.drawable.xls);
            defaultThumbnailMap.put("zip", R.drawable.zip);
            defaultThumbnailMap.put("cbz", R.drawable.cbz);
        }
        return defaultThumbnailMap;
    }

    static public Integer thumbnailUnknown() {
        return R.drawable.unknown_document;
    }

    static public Integer thumbnailDefault() {
        return R.drawable.book_default_cover;
    }

    static public Integer libraryThumbnailDefault() {
        return R.drawable.library_default_cover;
    }

    static public Bitmap loadThumbnail(Context context, int resId) {
        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(resId);
        return drawable == null ? null : drawable.getBitmap();
    }

    static public Bitmap loadDefaultThumbnailFromExtension(Context context, String ext) {
        if (StringUtils.isNullOrEmpty(ext)) {
            return loadThumbnail(context, thumbnailUnknown());
        }
        if (defaultThumbnailMapping().containsKey(ext)) {
            return loadThumbnail(context, defaultThumbnailMapping().get(ext));
        }
        return loadThumbnail(context, thumbnailDefault());
    }

    public static String getThumbnailFile(Context context, String sourceMD5, String thumbnailKind) {
        return getThumbnailFile(context, sourceMD5, thumbnailKind, preferred_extension);
    }

    public static String getThumbnailFile(Context context, String sourceMD5, String thumbnailKind, String extension) {
        return EnvironmentUtil.getExternalStorageAppCacheDirectory(context.getPackageName()) +
                File.separator + thumbnail_folder + File.separator + sourceMD5 + "." + thumbnailKind +
                "." + extension;
    }

    public static Bitmap getThumbnailBitmap(Context context, String sourceMD5, String thumbnailKind) {
        return BitmapUtils.loadBitmapFromFile(getThumbnailFile(context, sourceMD5, thumbnailKind));
    }

    public static Bitmap getThumbnailBitmap(Context context, Thumbnail thumbnail) {
        Bitmap bitmap = getThumbnailBitmap(context, thumbnail.getIdString(), thumbnail.getThumbnailKind().toString());
        if (bitmap == null) {
            bitmap = ThumbnailUtils.loadDefaultThumbnailFromExtension(context, FileUtils.getFileExtension(thumbnail.getOriginContentPath()));
        }
        return bitmap;
    }

    public static boolean saveThumbnailBitmap(Context context, Thumbnail thumbnail, Bitmap saveBitmap) {
        String path = getThumbnailFile(context, thumbnail.getIdString(), thumbnail.getThumbnailKind().toString());
        if (!FileUtils.ensureFileExists(path)) {
            return false;
        }
        boolean save = BitmapUtils.saveBitmap(generateBitmap(saveBitmap, thumbnail.getThumbnailKind()), path);
        if (save) {
            thumbnail.setOriginContentPath(path);
            thumbnail.save();
        }
        return save;
    }

    private static Bitmap generateBitmap(Bitmap bitmap, ThumbnailKind kind) {
        Bitmap scaleBitmap = bitmap;
        switch (kind) {
            case Original:
                scaleBitmap = bitmap;
                break;
            case Large:
                scaleBitmap = OnyxThumbnail.createLargeThumbnail(bitmap);
                break;
            case Middle:
                scaleBitmap = OnyxThumbnail.createMiddleThumbnail(bitmap);
                break;
            case Small:
                scaleBitmap = OnyxThumbnail.createSmallThumbnail(bitmap);
                break;
            default:
                assert (false);
                break;
        }
        return scaleBitmap;
    }

    public static boolean insertThumbnail(Context context, DataProviderBase dataProviderBase, String filePath,
                                          String associationId, ThumbnailKind kind, Bitmap bitmap) {
        return insertThumbnail(context, dataProviderBase, filePath, ThumbnailUtils.getThumbnailFile(context, associationId, kind.toString()),
                associationId, kind, bitmap);
    }

    public static boolean insertThumbnail(Context context, DataProviderBase dataProviderBase, String bookFilePath,
                                          String thumbnailFilePath, String associationId, ThumbnailKind kind, Bitmap bitmap) {
        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setThumbnailKind(kind);
        thumbnail.setOriginContentPath(bookFilePath);
        thumbnail.setIdString(associationId);
        thumbnail.setImageDataPath(thumbnailFilePath);
        dataProviderBase.saveThumbnailEntry(context, thumbnail);
        return insertThumbnailBitmap(thumbnail, bitmap);
    }

    public static boolean insertThumbnail(Context context, DataProviderBase dataProviderBase, String filePath,
                                          String associationId, Bitmap bitmap) {
        for (ThumbnailKind kind : ThumbnailKind.values()) {
            boolean success = insertThumbnail(context, dataProviderBase, filePath, associationId, kind, bitmap);
            if (kind.equals(ThumbnailKind.Large) && !success) {
                return false;
            }
        }
        return true;
    }

    public static boolean writeBitmapToThumbnailFile(File file, Bitmap transBitmap) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            if (transBitmap == null || transBitmap.isRecycled()) {
                return false;
            }
            Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
            if (FileUtils.isPngExtension(file)) {
                format = Bitmap.CompressFormat.PNG;
            } else if (FileUtils.isJpgExtension(file)) {
                format = Bitmap.CompressFormat.JPEG;
            }
            return transBitmap.compress(format, 100, os);
        } catch (FileNotFoundException e) {
            Log.w("writeBitmapToThumbnail", e);
        } finally {
            FileUtils.closeQuietly(os);
        }
        return false;
    }

    public static boolean insertThumbnailBitmap(Thumbnail thumbnail, Bitmap bmp) {
        Bitmap transBitmap = generateBitmap(bmp, thumbnail.getThumbnailKind());
        FileUtils.ensureFileExists(thumbnail.getImageDataPath());
        File file = new File(thumbnail.getImageDataPath());
        return writeBitmapToThumbnailFile(file, transBitmap);
    }

    public static Bitmap createLargeThumbnail(Bitmap bmp) {
        return createThumbnail(bmp, 512);
    }

    /**
     * 256x256 at most, or original bmp' size, if it's smaller than 256x256
     *
     * @param bmp
     * @return
     */
    public static Bitmap createMiddleThumbnail(Bitmap bmp)
    {
        return createThumbnail(bmp, 256);
    }

    /**
     * 128x128 at most, or original bmp' size, if it's smaller than 128x128
     *
     * @param bmp
     * @return
     */
    public static Bitmap createSmallThumbnail(Bitmap bmp)
    {
        return createThumbnail(bmp, 128);
    }

    private static Bitmap createThumbnail(Bitmap bmp, int limit) {
        if (bmp.getWidth() <= limit && bmp.getHeight() <= limit) {
            return bmp;
        }

        int w = limit;
        int h = limit;

        if (bmp.getWidth() >= bmp.getHeight()) {
            double z = (double)limit / bmp.getWidth();
            h = (int)(z * bmp.getHeight());
        }
        else {
            double z = (double)limit / bmp.getHeight();
            w = (int)(z * bmp.getWidth());
        }

        return Bitmap.createScaledBitmap(bmp, w, h, true);
    }

    public static CloseableReference<Bitmap> decodeFile(File file) throws IOException {
        return decodeStream(new FileInputStream(file), Bitmap.Config.ARGB_8888);
    }

    public static CloseableReference<Bitmap> decodeFile(File file, Bitmap.Config config) throws IOException {
        return decodeStream(new FileInputStream(file), config);
    }

    public static CloseableReference<Bitmap> decodeStream(InputStream inputStream, Bitmap.Config config) throws IOException {
        PoolFactory poolFactory = new PoolFactory(PoolConfig.newBuilder().build());
        PooledByteBuffer pooledByteBuffer = null;
        EncodedImage image = null;
        try {
            pooledByteBuffer = poolFactory.getPooledByteBufferFactory().newByteBuffer(inputStream);
            image = new EncodedImage(CloseableReference.of(pooledByteBuffer));
            return Fresco.getImagePipelineFactory().getPlatformDecoder().decodeFromEncodedImage(image,
                    config);
        } finally {
            FileUtils.closeQuietly(inputStream);
            FileUtils.closeQuietly(image);
            FileUtils.closeQuietly(pooledByteBuffer);
        }
    }

    public static boolean hasThumbnail(Context context, DataProviderBase dataProvider, String associationId) {
        Thumbnail thumbnail = getThumbnailEntry(context, dataProvider, associationId, ThumbnailKind.Large);
        return thumbnail != null && thumbnail.hasValidId();
    }

    public static Thumbnail getThumbnailEntry(Context context, DataProviderBase dataProvider, String associationId, ThumbnailKind kind) {
        return dataProvider.getThumbnailEntry(context, associationId, kind);
    }

    public static boolean updateThumbnailEntrySet(Context context, DataProviderBase dataProvider, String associationId, Bitmap originBitmap) {
        boolean success = true;
        for (ThumbnailKind thumbnailKind : ThumbnailKind.values()) {
            success &= updateThumbnailEntry(context, dataProvider, associationId, thumbnailKind, originBitmap);
        }
        return success;
    }

    public static boolean updateThumbnailEntry(Context context, DataProviderBase dataProvider, String associationId, ThumbnailKind kind, Bitmap originBitmap) {
        Thumbnail thumbnail = getThumbnailEntry(context, dataProvider, associationId, kind);
        if (thumbnail == null) {
            Log.w("update thumbnail", "detect null");
            return false;
        }
        thumbnail.setImageDataPath(ThumbnailUtils.getThumbnailFile(context, thumbnail.getIdString(), thumbnail.getThumbnailKind().toString()));
        dataProvider.saveThumbnailEntry(context, thumbnail);
        return insertThumbnailBitmap(thumbnail, originBitmap);
    }
}
