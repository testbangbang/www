package com.onyx.android.sdk.utils;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.os.Build;
import android.util.Log;
import com.onyx.android.sdk.data.Size;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    private static Paint paint = new Paint();

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

    static public void scaleBitmap(final Bitmap src, final Rect srcRegion,
                                   final Bitmap dst, final Rect dstRegion) {
        Canvas canvas = new Canvas(dst);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(src, srcRegion, dstRegion, paint);
    }

    /**
     * return null if failed
     *
     * @param stream
     * @return
     */
    static public boolean decodeBitmapSize(final InputStream stream, Size size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(stream, null, options);
            size.width = options.outWidth;
            size.height = options.outHeight;
            return true;
        } catch (Throwable tr) {
            Log.w(TAG, tr);
            return false;
        }
    }

    /**
     * return null if failed
     *
     * @param path
     * @return
     */
    static public boolean decodeBitmapSize(final String path, Size size) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(path);
            return decodeBitmapSize(stream, size);
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

    public static void clear(final Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE);
        }
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

    static public Bitmap fromGrayscale(final byte[] gray, int width, int height, int stride) {
        final int pixCount = width * height;
        int[] intGreyBuffer = new int[pixCount];
        int index = 0;
        for(int i=0; i < height; i++) {
            for (int j = 0; j < width; ++j) {
                int greyValue = (int) gray[i * stride + j] & 0xff;
                intGreyBuffer[index++] = 0xff000000 | (greyValue << 16) | (greyValue << 8) | greyValue;
            }
        }
        Bitmap grayScaledPic = Bitmap.createBitmap(intGreyBuffer, width, height, Bitmap.Config.ARGB_8888);
        return grayScaledPic;
    }

    static public byte[] toGrayScale(final Bitmap bitmapInArgb) {
        byte [] data = new byte[bitmapInArgb.getWidth() * bitmapInArgb.getHeight()];
        for(int y = 0; y < bitmapInArgb.getHeight(); ++y) {
            for(int x = 0; x < bitmapInArgb.getWidth(); ++x) {
                int value = bitmapInArgb.getPixel(x, y);
                byte gray = (byte) (0.299 * Color.red(value) + 0.587 * Color.green(value) + 0.114 * Color.blue(value));
                data[y * bitmapInArgb.getWidth() + x] = gray;
            }
        }
        return data;
    }


    static public byte[] cfa(final Bitmap bitmapInArgb, final Rect rect) {
        byte [] data = new byte[rect.width() * rect.height() * 4];
        for(int y = rect.top; y < rect.bottom; ++y) {
            for(int x = rect.left; x < rect.right; ++x) {
                int value = bitmapInArgb.getPixel(x, y);
                byte r = (byte)Color.red(value);
                byte g = (byte)Color.green(value);
                byte b = (byte)Color.blue(value);
                byte w = r;
                data[y * rect.width() + x] = w;
            }
        }
        return data;
    }

}
