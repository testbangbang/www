package com.onyx.kreader.plugins.images;

import android.graphics.*;

import com.onyx.kreader.host.math.PageUtils;
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
                renderToBitmap(src, bitmap, bitmapRegion, (int)imageInformation.width,
                        (int)imageInformation.height, x, y, width, height);
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
        Rect bitmapRegion = PageUtils.screenRegionToDoc(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
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

    private Matrix mapViewportToScreen(Rect viewportRegion, int docWidth, int docHeight, int x, int y, int width, int height) {
        Matrix matrix = new Matrix();
        float scaleX = width / (float)docWidth;
        float scaleY = height / (float)docHeight;
        matrix.postScale(scaleX, scaleY);
        int regionOffsetX = (int)(viewportRegion.left * scaleX) + x;
        int regionOffsetY = (int)(viewportRegion.top * scaleY) + y;
        matrix.postTranslate(regionOffsetX, regionOffsetY);
        return matrix;
    }

    private void renderToBitmap(Bitmap viewportBitmap, Bitmap dst, Rect viewportRegion, int docWidth, int docHeight, int x, int y, int width, int height) {
        Canvas canvas = new Canvas(dst);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        Matrix matrix = mapViewportToScreen(viewportRegion, docWidth, docHeight, x, y, width, height);
        canvas.drawBitmap(viewportBitmap, matrix, paint);
    }

}
