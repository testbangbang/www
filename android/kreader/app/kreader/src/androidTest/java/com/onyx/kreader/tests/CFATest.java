package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.kreader.plugins.neopdf.NeoPdfJniWrapper;
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

    public void testRender000() throws Exception {
        for(int i = 1; i <= 4; ++i) {
            final Bitmap origin = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/cfa-org/" + i + ".jpg");
            final Bitmap scaled = origin.createScaledBitmap(origin, 480, 640, true);
            final Bitmap result = Bitmap.createBitmap(960, 1280, Bitmap.Config.ARGB_8888);
            ImageUtils.toRgbwBitmap(result, scaled, 0);
            BitmapUtils.saveBitmap(result, "/mnt/sdcard/cfa-org/" + i + ".cfa.png");
        }
    }

    public void testRender0() throws Exception {
        int targetWidth = 200;
        int targetHeight = 200;
        final Bitmap origin = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/icons/user_boy.png");
        final Bitmap scaled = origin.createScaledBitmap(origin, targetWidth / 2, targetHeight / 2, true);
        final Bitmap result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        ImageUtils.toRgbwBitmap(result, scaled, 0);
        BitmapUtils.saveBitmap(result, "/mnt/sdcard/icons/user_boy_cfa.png");
    }

    public void testRender00() throws Exception {
        int targetWidth = 100;
        int targetHeight = 100;
        final Bitmap origin = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/icons/home_pic_display.png");
        final Bitmap scaled = origin.createScaledBitmap(origin, targetWidth / 2, targetHeight / 2, true);
        final Bitmap result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        ImageUtils.toRgbwBitmap(result, scaled, 0);
        BitmapUtils.saveBitmap(result, "/mnt/sdcard/icons/home_pic_display_cfa.png");
    }

    public void testRender1() throws Exception {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        String[] pathes = {"/mnt/sdcard/Books/cfa.pdf"};
        for (String path : pathes) {
            assertTrue(wrapper.nativeInitLibrary());
            assertTrue(wrapper.openDocument(path, null) == NeoPdfJniWrapper.NO_ERROR);
            int pageCount = wrapper.pageCount();
            assertTrue(pageCount > 0);
            ReaderBitmapImpl readerBitmap = null;
            for (int i = 0; i < pageCount; ++i) {
                float size[] = new float[2];
                assertTrue(wrapper.pageSize(i, size));
                assertTrue(size[0] > 0);
                assertTrue(size[1] > 0);
                if (readerBitmap == null) {
                    readerBitmap = new ReaderBitmapImpl(480, 640, Bitmap.Config.ARGB_8888);
                } else {
                    readerBitmap.update(480, 640, Bitmap.Config.ARGB_8888);
                }
                Bitmap bitmap = readerBitmap.getBitmap();
                long start = System.currentTimeMillis();
                assertTrue(wrapper.drawPage(i, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
                long end = System.currentTimeMillis();
                Log.i(TAG, "Performance testing: " + path + " page: " + i + " ts: " + (end - start));

                BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/Books/cfa.origin." + i + ".png");

                final Bitmap result = Bitmap.createBitmap(960, 1280, Bitmap.Config.ARGB_8888);
                ImageUtils.toRgbwBitmap(result, bitmap, 0);
                BitmapUtils.saveBitmap(result, "/mnt/sdcard/Books/cfa." + i + ".png");


            }
            readerBitmap.recycleBitmap();
            assertTrue(wrapper.closeDocument());
            assertTrue(wrapper.nativeDestroyLibrary());
        }
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
