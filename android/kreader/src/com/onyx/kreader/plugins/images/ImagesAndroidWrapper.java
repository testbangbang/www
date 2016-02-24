package com.onyx.kreader.plugins.images;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.onyx.kreader.utils.BitmapUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by joy on 2/22/16.
 */
public class ImagesAndroidWrapper implements ImagesWrapper {
    private static final String TAG = ImagesAndroidWrapper.class.getSimpleName();

    private static class BitmapInformation {
        public float width = 0;
        public float height = 0;
    }

    private static final HashMap<String, BitmapInformation> infoCache = new HashMap<String, BitmapInformation>();

    @Override
    public boolean clearBitmap(Bitmap bitmap) {
        return false;
    }

    public boolean pageSize(final String path, float[] size) {
        if (!infoCache.containsKey(path)) {
            if (!BitmapUtils.decodeBitmapSize(path, size)) {
                return false;
            }
            saveBitmapInformation(path, size);
        }
        loadBitmapInformation(path, size);
        return true;
    }

    public boolean drawImage(final String imagePath, int x, int y, int width, int height, int rotation, final Bitmap bitmap) {
        try {
            float[] size = new float[2];
            if (!pageSize(imagePath, size)) {
                return false;
            }

            Rect bitmapRegion = locatePageRegion((int)widthOfSize(size), (int)heightOfSize(size),
                    x, y, width, height, rotation, bitmap);

            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(imagePath, true);
            Bitmap src = decoder.decodeRegion(bitmapRegion, null);
            if (src == null) {
                return false;
            }
            try {
                renderToBitmap(src, bitmap, x, y, width, height);
            } finally {
                src.recycle();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Rect locatePageRegion(int pageWidth, int pageHeight, int x, int y, int width, int height, int rotation, Bitmap bitmap) {
        Rect bitmapRegion = deviceRegionToPage(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                pageWidth, pageHeight, x, y, width, height, rotation);
        Rect pageRect = new Rect(0, 0, pageWidth, pageHeight);
        pageRect.intersect(bitmapRegion);
        return pageRect;
    }

    @Override
    public boolean closeImage(String path) {
        return false;
    }

    @Override
    public void closeAll() {

    }

    private void saveBitmapInformation(String path, float[] size) {
        BitmapInformation info = new BitmapInformation();
        info.width = size[0];
        info.height = size[1];
        infoCache.put(path, info);
    }

    private void loadBitmapInformation(String path, float[] size) {
        BitmapInformation info = infoCache.get(path);
        size[0] = info.width;
        size[1] = info.height;
    }

    private float widthOfSize(float[] size) {
        return size[0];
    }

    private float heightOfSize(float[] size) {
        return size[1];
    }

    private void renderToBitmap(Bitmap src, Bitmap dst, int x, int y, int width, int height) {
        Canvas canvas = new Canvas(dst);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        Matrix matrix = new Matrix();
        matrix.postScale(width / (float)src.getWidth(), height / (float)src.getHeight());
        matrix.postTranslate(x, y);
        canvas.drawBitmap(src, matrix, paint);
    }

    /**
     * TODO ignore rotation first
     *
     * @param point
     * @param pageWidth
     * @param pageHeight
     * @param x
     * @param y
     * @param width
     * @param height
     * @param rotation
     * @return
     */
    private Point devicePointToPage(Point point, int pageWidth, int pageHeight, int x, int y, int width, int height, int rotation) {
        double scaleX = width / (double)pageWidth;
        double scaleY = height / (double)pageHeight;
        return new Point((int)((point.x - x) / scaleX), (int)((point.y - y) / scaleY));
    }

    private Rect deviceRegionToPage(Rect region, int pageWidth, int pageHeight, int x, int y, int width, int height, int rotation) {
        Point topLeft = devicePointToPage(new Point(region.left, region.top), pageWidth, pageHeight, x, y, width, height, rotation);
        Point rightBottom = devicePointToPage(new Point(region.right, region.bottom), pageWidth, pageHeight, x, y, width, height, rotation);
        return new Rect(topLeft.x, topLeft.y, rightBottom.x, rightBottom.y);
    }

}
