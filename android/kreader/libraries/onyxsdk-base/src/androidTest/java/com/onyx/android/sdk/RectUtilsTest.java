package com.onyx.android.sdk;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by joy on 6/1/17.
 */

public class RectUtilsTest extends ApplicationTestCase<Application> {

    public RectUtilsTest() {
        super(Application.class);
    }

    private boolean contains(final List<RectF> list, float x, float y) {
        for (RectF r : list) {
            if (r.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void saveBitmap(final RectF rect, final List<RectF> list, final List<RectF> result) {
        Bitmap bmp = Bitmap.createBitmap((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.WHITE);

        Random rand = new Random(System.currentTimeMillis());

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        for (int j = 0; j < list.size(); j++) {
            RectF r = list.get(j);
            int red = rand.nextInt(255);
            int green = rand.nextInt(255);
            int blue = rand.nextInt(255);
            paint.setARGB(128, red, green, blue);
            canvas.drawRect(r, paint);
            paint.setAlpha(255);
            paint.setColor(Color.BLACK);
            canvas.drawText("X" + j, r.left + 10, r.top + 10, paint);
        }

        for (int j = 0; j < result.size(); j++) {
            RectF r = result.get(j);
            int red = rand.nextInt(255);
            int green = rand.nextInt(255);
            int blue = rand.nextInt(255);
            paint.setStrokeWidth(2);
            paint.setARGB(64, red, green, blue);
            canvas.drawRect(r, paint);
            paint.setAlpha(255);
            paint.setColor(Color.BLACK);
            canvas.drawText("Z" + j, r.left + 10, r.top + 10, paint);
        }

        BitmapUtils.saveBitmap(bmp, new File(getContext().getFilesDir(), "rect.png").getAbsolutePath());
    }

    public void testCutRectByExcludingRegions() {
        for (int i = 0; i < 5; i++) {
            RectF rect = new RectF(0, 0, 1000, 1000);
            List<RectF> list = new ArrayList<>();

            Random rand = new Random(System.currentTimeMillis());

            int n = rand.nextInt(20) + 1;
            for (int j = 0; j < n; j++) {
                float x = rand.nextInt((int) rect.width());
                float y = rand.nextInt((int) rect.height());
                float width = rand.nextInt((int) (rect.width() - x));
                float height = rand.nextInt((int) (rect.height() - y));
                list.add(new RectF(x, y, x + width, y + height));
            }

//            list.add(new RectF(450, 10, 900, 870));
//            list.add(new RectF(600, 560, 770, 980));
//            list.add(new RectF(370, 460, 690, 900));

            List<RectF> copy = new ArrayList<>();
            for (RectF r : list) {
                Debug.e(getClass(), "excluding: " + r);
                copy.add(new RectF(r));
            }
            List<RectF> result = RectUtils.cutRectByExcludingRegions(rect, copy);
            Debug.e(getClass(), "result count: " + result.size());

            for (RectF r : result) {
                Debug.e(getClass(), "result: " + r.toString());
            }

            for (int j = 0; j < (int)rect.width(); j++) {
                for (int k = 0; k < (int)rect.height(); k++) {
                    assertTrue(contains(list, i, j) || contains(result, i, j));
                    assertFalse(contains(list, i, j) && contains(result, i, j));
                }
            }
        }
    }

}
