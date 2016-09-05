package com.onyx.android.sdk.data.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by suicheng on 2016/9/5.
 */
public class ThumbnailUtils {
    public static final String thumbnail_folder = ".thumbnails";
    public static final String preferred_extension = ".png";


    static public Integer thumbnailDefault() {
        return R.drawable.library_default_cover;
    }

    static public Integer subLibraryThumbnailDefault() {
        return R.drawable.sub_library_default_cover;
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
        if (FileUtils.fileExist(path)) {
            return true;
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
