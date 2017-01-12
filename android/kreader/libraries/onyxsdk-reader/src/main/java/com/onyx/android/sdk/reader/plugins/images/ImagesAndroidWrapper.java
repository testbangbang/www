package com.onyx.android.sdk.reader.plugins.images;

import android.graphics.*;
import android.util.Log;

import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by joy on 2/22/16.
 */
public class ImagesAndroidWrapper implements ImagesWrapper {

    private static final String TAG = ImagesAndroidWrapper.class.getSimpleName();
    private static final HashMap<String, ImageInformation> infoCache = new HashMap<String, ImageInformation>();

    public static boolean drawImage(final InputStream stream, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap) {
        int pageWidth = (int)(positionRect.width() / scale);
        int pageHeight = (int)(positionRect.height() / scale);

        try {
            Rect bitmapRegion = new Rect((int) (visibleRect.left / scale), (int) (visibleRect.top / scale),
                    (int) (visibleRect.right / scale), (int) (visibleRect.bottom / scale));

            Bitmap src;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPreferQualityOverSpeed = true;
            options.inMutable = true; // set mutable to be true, so we can always get a copy of the bitmap with Bitmap.createBitmap()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // not using BitmapRegionDecoder because it only supports JPEG and PNG
            Bitmap bmp = BitmapFactory.decodeStream(stream, null, options);
            if (bmp == null) {
                return false;
            }
            try {
                src = Bitmap.createBitmap(bmp, bitmapRegion.left, bitmapRegion.top,
                        bitmapRegion.width(), bitmapRegion.height());
            } finally {
                bmp.recycle();
            }
            if (src == null) {
                return false;
            }
            try {
                renderToBitmap(src, bitmap, bitmapRegion, pageWidth, pageHeight,
                        (int) displayRect.left, (int) displayRect.top,
                        (int) displayRect.width(), (int) displayRect.height());
            } finally {
                src.recycle();
            }
            return true;
        } catch (Throwable tr) {
            Log.w(TAG, tr);
        }

        return false;
    }

    public ImageInformation imageInfo(final String path) {
        if (!infoCache.containsKey(path)) {
            ImageInformation imageInformation = new ImageInformation();
            if (!BitmapUtils.decodeBitmapSize(path, imageInformation.getSize())) {
                return null;
            }
            saveImageInformation(path, imageInformation);
        }
        return loadBitmapInformation(path);
    }

    public boolean drawImage(final String imagePath, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(imagePath);
            return drawImage(stream, scale, rotation, displayRect, positionRect, visibleRect, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            FileUtils.closeQuietly(stream);
        }
    }

    @Override
    public boolean closeImage(String path) {
        return false;
    }

    @Override
    public void closeAll() {

    }

    private void saveImageInformation(String path, ImageInformation imageInformation) {
        infoCache.put(path, imageInformation);
    }

    private ImageInformation loadBitmapInformation(String path) {
        return infoCache.get(path);
    }

    private static Matrix mapViewportToScreen(Rect viewportRegion, int docWidth, int docHeight, int x, int y, int width, int height) {
        Matrix matrix = new Matrix();
        float scaleX = width / (float)docWidth;
        float scaleY = height / (float)docHeight;
        matrix.postScale(scaleX, scaleY);
        int regionOffsetX = (int)(viewportRegion.left * scaleX) + x;
        int regionOffsetY = (int)(viewportRegion.top * scaleY) + y;
        matrix.postTranslate(regionOffsetX, regionOffsetY);
        return matrix;
    }

    private static void renderToBitmap(Bitmap viewportBitmap, Bitmap dst, Rect viewportRegion, int docWidth, int docHeight, int x, int y, int width, int height) {
        Canvas canvas = new Canvas(dst);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
//        paint.setAntiAlias(true);
//        paint.setDither(true);
        Matrix matrix = mapViewportToScreen(viewportRegion, docWidth, docHeight, x, y, width, height);
        canvas.drawBitmap(viewportBitmap, matrix, paint);
    }

}
