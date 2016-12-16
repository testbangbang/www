package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;

import com.onyx.android.sdk.utils.BitmapUtils;

/**
 * Created by zhuzeng on 12/12/2016.
 */

public class CFATest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    static private String TAG = CFATest.class.getSimpleName();
    private boolean performanceTest = false;

    public CFATest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void testRender2() throws Exception {
        //final Bitmap origin = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/slide/ab2.png");
        final Bitmap origin = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/slide/ScreenShot012_42.jpg");
        final Bitmap scaled = origin.createScaledBitmap(origin, 480, 640, true);
        BitmapUtils.saveBitmap(scaled, "/mnt/sdcard/Books/ab-scaled.png");

        final Bitmap result = Bitmap.createBitmap(960, 1280, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < scaled.getHeight(); ++y) {
            for(int x = 0; x < scaled.getWidth(); ++x) {
                int color = scaled.getPixel(x, y);
                int nx = 2 * x;
                int ny = 2 * y;
                int b = Color.blue(color);
                int g = Color.green(color);
                int r = Color.red(color);
                int w = (((r * 299) + (g * 587) + (b * 114)) / 1000) & 0xff;
                result.setPixel(nx, ny, Color.argb(0xff, b, b, b));
                result.setPixel(nx + 1, ny, Color.argb(0xff, g, g, g));
                result.setPixel(nx, ny + 1, Color.argb(0xff, w, w, w));
                result.setPixel(nx + 1, ny + 1, Color.argb(0xff, r, r, r));
            }
        }

        BitmapUtils.saveBitmap(result, "/mnt/sdcard/Books/final8.png");

    }


}
