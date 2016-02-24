package com.onyx.kreader.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.hardware.Camera;
import android.util.Log;
import com.onyx.kreader.plugins.images.ImagesWrapper;

import java.io.FileOutputStream;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    static public Bitmap loadBitmapFromFile(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (!FileUtils.fileExist(path)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    static public boolean saveBitmap(Bitmap bitmap, final String path) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    static public boolean decodeBitmapSize(final String path, final ImagesWrapper.ImageInformation information) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(path, options);
            information.width = options.outWidth;
            information.height = options.outHeight;
            return true;
        } catch (Throwable tr) {
            Log.w(TAG, tr);
            return false;
        }
    }

}
