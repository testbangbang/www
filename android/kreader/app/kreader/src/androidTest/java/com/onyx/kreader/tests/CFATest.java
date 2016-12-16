package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;

import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.kreader.utils.ImageUtils;

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
        final Bitmap origin = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/slide/shutterstock_104688953_smalll_tweaked.jpg");
        final Bitmap scaled = origin.createScaledBitmap(origin, 480, 640, true);
        BitmapUtils.saveBitmap(scaled, "/mnt/sdcard/Books/ab-scaled.png");

        final Bitmap result = Bitmap.createBitmap(960, 1280, Bitmap.Config.ARGB_8888);
        ImageUtils.toRgbwBitmap(result, scaled, 0);
        BitmapUtils.saveBitmap(result, "/mnt/sdcard/Books/final11.png");
    }


}
