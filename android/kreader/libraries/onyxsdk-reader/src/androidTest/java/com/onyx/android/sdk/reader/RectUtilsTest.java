package com.onyx.android.sdk.reader;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.reader.utils.RectUtils;
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

    private float calculateSquare(List<RectF> list) {
        // TODO square result not always right, need improve
        float square = 0;
        for (RectF r : list) {
            float s = r.width() * r.height();
            square += s;
        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                RectF r1 = new RectF(list.get(i));
                RectF r2 = new RectF(list.get(j));
                if (r1.intersect(r2)) {
                    square -= r1.width() * r1.height();
                }
            }
        }

        return square;
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
            float excludingSquare = calculateSquare(list);
            Debug.e(getClass(), "excluding square: " + excludingSquare);
            float resultSquare = calculateSquare(result);
            Debug.e(getClass(), "result square: " + resultSquare);

            float square = excludingSquare + resultSquare;
            if (Float.compare(rect.width() * rect.height(), square) == 0) {
                Debug.e(getClass(), "success!");
                return;
            }

            Bitmap bmp = Bitmap.createBitmap((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
            bmp.eraseColor(Color.WHITE);

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
            Debug.e(getClass(), "failed!");
            assertEquals(rect.width() * rect.height(), square);
        }
    }

}
