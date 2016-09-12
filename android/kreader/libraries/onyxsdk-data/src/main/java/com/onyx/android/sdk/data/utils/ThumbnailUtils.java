package com.onyx.android.sdk.data.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suicheng on 2016/9/5.
 */
public class ThumbnailUtils {
    public static final String thumbnail_folder = ".thumbnails";
    public static final String preferred_extension = ".png";

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
        String thumbnail_file = EnvironmentUtil.getExternalStorageAppCacheDirectory(context.getPackageName()) +
                File.separator + thumbnail_folder + File.separator + sourceMD5 + "." + thumbnailKind + preferred_extension;
        return thumbnail_file;
    }

    public static Bitmap getThumbnailBitmap(Context context, String sourceMD5, String thumbnailKind) {
        return BitmapUtils.loadBitmapFromFile(getThumbnailFile(context, sourceMD5, thumbnailKind));
    }

    public static Bitmap getThumbnailBitmap(Context context, Thumbnail thumbnail) {
        return getThumbnailBitmap(context, thumbnail.getSourceMD5(), thumbnail.getThumbnailKind().toString());
    }

    public static boolean saveThumbnailBitmap(Context context, Thumbnail thumbnail, Bitmap saveBitmap) {
        String path = getThumbnailFile(context, thumbnail.getSourceMD5(), thumbnail.getThumbnailKind().toString());
        if (!FileUtils.ensureFileExists(path)) {
            return false;
        }
        boolean save = BitmapUtils.saveBitmap(generateBitmap(saveBitmap, thumbnail.getThumbnailKind()), path);
        if (save) {
            thumbnail.setPath(path);
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
        }
        return scaleBitmap;
    }
}
