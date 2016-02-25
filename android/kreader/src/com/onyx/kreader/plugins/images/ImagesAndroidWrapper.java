package com.onyx.kreader.plugins.images;

import android.graphics.*;

import com.onyx.kreader.utils.BitmapUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by joy on 2/22/16.
 */
public class ImagesAndroidWrapper implements ImagesWrapper {

    private static final String TAG = ImagesAndroidWrapper.class.getSimpleName();
    private static final HashMap<String, ImageInformation> infoCache = new HashMap<String, ImageInformation>();

    public ImageInformation imageInfo(final String path) {
        if (!infoCache.containsKey(path)) {
            ImageInformation imageInformation = new ImageInformation();
            if (!BitmapUtils.decodeBitmapSize(path, imageInformation)) {
                return null;
            }
            saveImageInformation(path, imageInformation);
        }
        return loadBitmapInformation(path);
    }

    public boolean drawImage(final String imagePath, int x, int y, int width, int height, int rotation, final Bitmap bitmap) {
        try {
            ImageInformation imageInformation;
            if ((imageInformation = imageInfo(imagePath)) == null) {
                return false;
            }

            Rect bitmapRegion = locatePageRegion((int)imageInformation.width, (int)imageInformation.height,
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

    private void saveImageInformation(String path, ImageInformation imageInformation) {
        infoCache.put(path, imageInformation);
    }

    private ImageInformation loadBitmapInformation(String path) {
        return infoCache.get(path);
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
