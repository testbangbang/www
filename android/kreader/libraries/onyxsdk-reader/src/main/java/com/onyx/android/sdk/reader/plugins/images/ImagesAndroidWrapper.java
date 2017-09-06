package com.onyx.android.sdk.reader.plugins.images;

import android.graphics.*;
import android.util.Log;

import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by joy on 2/22/16.
 */
public class ImagesAndroidWrapper implements ImagesWrapper {

    private static final String TAG = ImagesAndroidWrapper.class.getSimpleName();
    private static final HashMap<String, ImageInformation> infoCache = new HashMap<String, ImageInformation>();

    public static boolean drawImage(final InputStream stream, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap) {
        ReaderBitmapReferenceImpl srcImage = null;
        try {
            srcImage = ReaderBitmapReferenceImpl.decodeStream(stream, ReaderBitmapReferenceImpl.DEFAULT_CONFIG);
            drawBitmap(srcImage.getBitmap(), bitmap, scale, rotation, displayRect, positionRect, visibleRect);
            return true;
        } catch (Throwable tr) {
            Log.w(TAG, tr);
        } finally {
            FileUtils.closeQuietly(srcImage);
        }

        return false;
    }

    private static void drawBitmap(final Bitmap src, final Bitmap dst, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect) {
        int pageWidth = (int)(positionRect.width() / scale);
        int pageHeight = (int)(positionRect.height() / scale);

        Rect bitmapRegion = new Rect((int) (visibleRect.left / scale), (int) (visibleRect.top / scale),
                (int) (visibleRect.right / scale), (int) (visibleRect.bottom / scale));
        ReaderBitmapReferenceImpl subImage = createSubImage(src, bitmapRegion);

        try {
            renderToBitmap(subImage.getBitmap(), dst, bitmapRegion, pageWidth, pageHeight,
                    (int) displayRect.left, (int) displayRect.top,
                    (int) displayRect.width(), (int) displayRect.height());
        } finally {
            FileUtils.closeQuietly(subImage);
        }
    }

    public ImageInformation imageInfo(final String path) {
        if (!infoCache.containsKey(path)) {
            ImageInformation imageInformation = new ImageInformation();
            if (isTiffImage(path)) {
                final TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
                options.inJustDecodeBounds = true;
                TiffBitmapFactory.decodeFile(new File(path), options);
                imageInformation.getSize().width = options.outWidth;
                imageInformation.getSize().height = options.outHeight;
            } else if (!BitmapUtils.decodeBitmapSize(path, imageInformation.getSize())) {
                return null;
            }
            saveImageInformation(path, imageInformation);
        }
        return loadBitmapInformation(path);
    }

    public boolean drawImage(final String imagePath, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap) {
        if (isTiffImage(imagePath)) {
            final TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
            Bitmap src = TiffBitmapFactory.decodeFile(new File(imagePath), options);
            drawBitmap(src, bitmap, scale, rotation, displayRect, positionRect, visibleRect);
            return true;
        }

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

    private static ReaderBitmapReferenceImpl createSubImage(Bitmap src, Rect region) {
        ReaderBitmapReferenceImpl subImage = ReaderBitmapReferenceImpl.create(region.width(), region.height(),
                ReaderBitmapReferenceImpl.DEFAULT_CONFIG);
        Canvas canvas = new Canvas(subImage.getBitmap());
        canvas.drawBitmap(src,
                region,
                new Rect(0, 0, region.width(), region.height()),
                new Paint(Paint.FILTER_BITMAP_FLAG));
        return subImage;
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

    private static boolean isTiffImage(String path) {
        return path.toLowerCase(Locale.getDefault()).endsWith(".tif") ||
                path.toLowerCase(Locale.getDefault()).endsWith(".tiff");
    }

}
