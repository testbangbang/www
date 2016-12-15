package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.PowerManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.plugins.neopdf.NeoPdfJniWrapper;
import com.onyx.kreader.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 12/12/2016.
 */

public class CFATest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    static private String TAG = CFATest.class.getSimpleName();
    private boolean performanceTest = false;

    public CFATest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void noTestRender() throws Exception {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/cfa.pdf", null) == NeoPdfJniWrapper.NO_ERROR);

        int pageCount = wrapper.pageCount();
        assertTrue(pageCount > 0);

        int page = 0;
        float size[] = new float[2];
        assertTrue(wrapper.pageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);

        // render origin image
        Bitmap argb = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, argb.getWidth(), argb.getHeight(), 0, argb));
        BitmapUtils.saveBitmap(argb, "/mnt/sdcard/Books/argb.png");

        // save gray bin file.
        int strideInBytes = argb.getWidth() * 4;
        final byte [] gray = new byte[strideInBytes * argb.getHeight()];
        ImageUtils.toGrayScale(argb, gray, strideInBytes);
        Bitmap temp = BitmapUtils.fromGrayscale(gray, argb.getWidth(), argb.getHeight(), strideInBytes);
        BitmapUtils.saveBitmap(temp, "/mnt/sdcard/Books/gray.png");

        // copy rect of source bitmap to dst bitmap with cfa applied.
        Bitmap scaledColor = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/Books/color.png");
        scaledColor = Bitmap.createScaledBitmap(scaledColor, 100, 100, true);
        BitmapUtils.saveBitmap(scaledColor, "/mnt/sdcard/Books/scaledColor.png");

        int cw = scaledColor.getWidth() * 2;
        int ch = scaledColor.getHeight() * 2;
        int colorStrideInBytes = cw * 4;
        final byte [] rgbw = new byte[colorStrideInBytes * ch * 2];
        ImageUtils.toRgbw(scaledColor, rgbw, colorStrideInBytes);
        temp = BitmapUtils.fromGrayscale(rgbw, cw, ch, colorStrideInBytes);
        BitmapUtils.saveBitmap(temp, "/mnt/sdcard/Books/rgbw.png");

        // blend
        ImageUtils.blend(gray, strideInBytes, rgbw, 0, 0, cw, ch, colorStrideInBytes);
        temp = BitmapUtils.fromGrayscale(gray, argb.getWidth(), argb.getHeight(), strideInBytes);
        BitmapUtils.saveBitmap(temp, "/mnt/sdcard/Books/grayblend.png");
        FileUtils.saveContentToFile(gray, new File("/mnt/sdcard/Books/final.bin"));

        // recreate image from blended data.
        final Bitmap finalBitmap = Bitmap.createBitmap(960, 1280, Bitmap.Config.ARGB_8888);
        for(int i = 0; i < finalBitmap.getWidth() / 3; ++i) {
            int step = 20;
            int count = 0;
            for(int j = count * step; j < count * step + step; ++j) {
                int x = i * 2;
                int y = j * 2;
                finalBitmap.setPixel(x,     y,      Color.WHITE);   // b
                finalBitmap.setPixel(x + 1, y,      Color.BLACK);   // g
                finalBitmap.setPixel(x,     y + 1,  0xff0f0f0f);   // w
                finalBitmap.setPixel(x + 1, y + 1,  Color.BLACK);   // r
            }

            ++count;
            for(int j = count * step; j < count * step + step; ++j) {
                int x = i * 2;
                int y = j * 2;
                finalBitmap.setPixel(x,     y,      Color.BLACK);   // r
                finalBitmap.setPixel(x + 1, y,      Color.WHITE);   // g
                finalBitmap.setPixel(x,     y + 1,  0xff808080);   // w
                finalBitmap.setPixel(x + 1, y + 1,  Color.BLACK);   // b
            }

            ++count;
            for(int j = count * step; j < count * step + step; ++j) {
                int x = i * 2;
                int y = j * 2;
                finalBitmap.setPixel(x,     y,      Color.BLACK);   // b
                finalBitmap.setPixel(x + 1, y,      Color.BLACK);   // g
                finalBitmap.setPixel(x,     y + 1,  0xff505050);   // w
                finalBitmap.setPixel(x + 1, y + 1,  Color.WHITE);   // r
            }

            ++count;
            for(int j = count * step; j < count * step + step; ++j) {
                int x = i * 2;
                int y = j * 2;
                finalBitmap.setPixel(x,     y,      Color.BLACK);   // b
                finalBitmap.setPixel(x + 1, y,      Color.BLACK);   // g
                finalBitmap.setPixel(x,     y + 1,  Color.BLACK);   // w
                finalBitmap.setPixel(x + 1, y + 1,  Color.WHITE);   // r
            }

            ++count;
            for(int j = count * step; j < count * step + step; ++j) {
                int x = i * 2;
                int y = j * 2;
                finalBitmap.setPixel(x,     y,      Color.BLACK);   // red
                finalBitmap.setPixel(x + 1, y,      Color.WHITE);   // green
                finalBitmap.setPixel(x,     y + 1,  0xff808080);   // white
                finalBitmap.setPixel(x + 1, y + 1,  Color.BLACK);   // blue
            }

            ++count;
            for(int j = count * step; j < count * step + step; ++j) {
                int x = i * 2;
                int y = j * 2;
                finalBitmap.setPixel(x,     y,      0xff808080);   // red
                finalBitmap.setPixel(x + 1, y,      Color.BLACK);   // white
                finalBitmap.setPixel(x,     y + 1,  Color.WHITE);   // green
                finalBitmap.setPixel(x + 1, y + 1,  Color.BLACK);   // blue
            }

        }
        BitmapUtils.saveBitmap(finalBitmap, "/mnt/sdcard/Books/final.png");

        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());

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
