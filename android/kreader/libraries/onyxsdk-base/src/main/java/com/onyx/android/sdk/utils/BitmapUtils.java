package com.onyx.android.sdk.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onyx.android.sdk.data.Size;

import java.io.ByteArrayOutputStream;
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

    static public void scaleToFitCenter(final Bitmap src, final Bitmap dst) {
        Rect srcBitmapRegion = new Rect(0, 0, src.getWidth(), src.getHeight());
        Rect dstScaleRegion = null;
        int offsetValue = 0;
        int scalerResult = 0;
        float ratioSrc = src.getWidth()/(float)src.getHeight();
        float ratioDest = dst.getWidth()/(float)dst.getHeight();

        if(ratioSrc >= ratioDest) {
            scalerResult = Math.round(dst.getWidth()/ratioSrc);
            offsetValue = Math.abs((dst.getHeight()-scalerResult)/2);
            dstScaleRegion = new Rect(0, offsetValue, dst.getWidth(), scalerResult + offsetValue);
        } else {
            scalerResult = Math.round(dst.getHeight() * ratioSrc);
            offsetValue = Math.abs((dst.getWidth() - scalerResult) / 2);
            dstScaleRegion = new Rect(offsetValue, 0, scalerResult + offsetValue, dst.getHeight());
        }
        BitmapUtils.scaleBitmap(src,srcBitmapRegion ,dst, dstScaleRegion);
    }

    public static Bitmap createScaledBitmap(final Bitmap origin, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) origin.getWidth();
        float ratioY = newHeight / (float) origin.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(origin, middleX - origin.getWidth() / 2, middleY - origin.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
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

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        return bitmapToBytes(bitmap, Bitmap.CompressFormat.PNG);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(format, 100, os);
        return os.toByteArray();
    }

    /**
     *
     * @param renderTargetHeight
     * @param renderTargetWidth
     * @param sourceHeight
     * @param sourceWidth
     * @param zoomToWidth if true means zoomToWidth,otherwise means zoomToHeight
     * @return
     */
    public static Rect getScaleInSideAndCenterRect(int renderTargetHeight, int renderTargetWidth,
                                                   int sourceHeight, int sourceWidth, boolean zoomToWidth) {
        Rect resultRect;
        float ratio;
        if (zoomToWidth) {
            ratio = ((float) (renderTargetWidth - 1)) / ((float) (sourceWidth - 1));
            int targetHeight = (int) ((renderTargetHeight - 1) * ratio);
            int top = (sourceHeight - 1 - targetHeight) / 2;
            resultRect = new Rect(0, top, renderTargetWidth - 1,
                    targetHeight + top);
        } else {
            ratio = ((float) (renderTargetHeight - 1)) / ((float) (sourceHeight - 1));
            int targetWidth = (int) ((renderTargetWidth - 1) * ratio);
            int left = (sourceWidth - 1 - targetWidth) / 2;
            resultRect = new Rect(left, 0, targetWidth + left,
                    renderTargetHeight - 1);
        }
        return resultRect;
    }

    public static Bitmap rotateBmp(Bitmap bmp, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    public static Bitmap buildBitmapFromText(String targetString, int height, int textSize,
                                             boolean boldText, boolean saveToDisk,
                                             boolean overrideFilePermission,
                                             boolean needRotation, int rotationAngle,
                                             @Nullable String path) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3);
        paint.setTextSize(textSize);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(boldText);
        int width = StringUtils.getTextWidth(paint, targetString);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Rect targetRect = new Rect(0, 0, width, height);
        Canvas canvas = new Canvas(bitmap);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (height - fontMetrics.bottom - fontMetrics.top) / 2;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(targetString, targetRect.centerX(), baseline, paint);
        if (needRotation){
            bitmap = rotateBmp(bitmap, rotationAngle);
        }
        if (saveToDisk) {
            saveBitmap(bitmap, path);
            if (overrideFilePermission) {
                ShellUtils.execCommand("busybox chmod 644 " + path, false);
            }
        }
        return bitmap;
    }

}
