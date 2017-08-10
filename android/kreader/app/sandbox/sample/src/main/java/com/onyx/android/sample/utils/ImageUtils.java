package com.onyx.android.sample.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 10/8/2017.
 */

public class ImageUtils {
    private List<Rect> list = new ArrayList<>();
    public static int threshold = 10;

    public void diff(final String first, final String second) {
        list.clear();
        final Bitmap firstBitmap = loadBitmapFromFile(first);
        final Bitmap secondBitmap = loadBitmapFromFile(second);

        for(int y = 0; y < firstBitmap.getHeight(); ++y) {
            for(int x = 0; x < firstBitmap.getWidth(); ++x) {
                int v1 = firstBitmap.getPixel(x, y);
                int v2 = secondBitmap.getPixel(x, y);
                if (v1 == v2) {
                    continue;
                }
                tryToMerge(x, y, threshold);
            }
        }

        drawRectanglesOnBitmap(secondBitmap, list);
        BitmapUtils.saveBitmap(secondBitmap, second + ".diff.png");
    }

    private boolean tryToMerge(int x, int y, int threshold) {
        for(int i = list.size() - 1; i >= 0; --i) {
            Rect rect = list.get(i);
            if (contains(rect, x, y, threshold)) {
                rect.union(x, y);
                return true;
            }
        }
        Rect rect = new Rect(x, y, x + 1, y + 1);
        list.add(rect);
        return false;
    }

    public static boolean contains(final Rect rect, int x, int y, int threshold) {
        return x >= rect.left - threshold &&
                x < rect.right + threshold &&
                y >= rect.top - threshold &&
                y < rect.bottom + threshold;
    }

    public static void union(final Rect rect, int x, int y) {
        rect.union(x, y);
    }

    public static void drawRectanglesOnBitmap(final Bitmap bitmap, final List<Rect> list) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0F);
        for(Rect rect : list) {
            canvas.drawRect(rect, paint);
        }
    }


    public static Bitmap loadBitmapFromFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

}
