package com.onyx.kreader.utils;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.os.Build;
import android.util.Log;
import com.onyx.kreader.plugins.images.ImagesWrapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

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

    static public boolean decodeBitmapSize(final InputStream stream, final ImagesWrapper.ImageInformation information) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(stream, null, options);
            information.width = options.outWidth;
            information.height = options.outHeight;
            return true;
        } catch (Throwable tr) {
            Log.w(TAG, tr);
            return false;
        }
    }

    static public boolean decodeBitmapSize(final String path, final ImagesWrapper.ImageInformation information) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(path);
            return decodeBitmapSize(stream, information);
        } catch (Throwable tr) {
            Log.w(TAG, tr);
            return false;
        } finally {
            FileUtils.closeQuietly(stream);
        }
    }

    static public void drawRectOnBitmap(Bitmap bmp, RectF rect) {
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(rect, paint);
    }

    @SuppressLint("NewApi")
    public static int getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        // There's a known issue in KitKat where getAllocationByteCount() can throw an NPE. This was
        // apparently fixed in MR1: http://bit.ly/1IvdRpd. So we do a version check here, and
        // catch any potential NPEs just to be safe.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            try {
                return bitmap.getAllocationByteCount();
            } catch (NullPointerException npe) {
                // Swallow exception and try fallbacks.
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        // Estimate for earlier platforms.
        return bitmap.getHeight() * bitmap.getRowBytes();
    }

    static public boolean isValid(final Bitmap bitmap) {
        return bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0 && !bitmap.isRecycled();
    }

}
