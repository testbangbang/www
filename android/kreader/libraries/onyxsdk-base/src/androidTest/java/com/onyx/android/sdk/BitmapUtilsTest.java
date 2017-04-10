package com.onyx.android.sdk;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by 12345 on 2017/4/6.
 */

public class BitmapUtilsTest extends ApplicationTestCase<Application> {
    public BitmapUtilsTest() {
        super(Application.class);
    }

    private Bitmap loadBitmapFromStream(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferQualityOverSpeed = true;
        options.inMutable = true; // set mutable to be true, so we can always get a copy of the bitmap with Bitmap.createBitmap()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(is, null, options);
    }

    private Bitmap loadSrcBitmapFromLocalFile(String format) throws FileNotFoundException {
        if("png".equals(format)) {
            return loadBitmapFromStream(new FileInputStream("/sdcard/Pictures/src.png"));
        }else if("bmp".equals(format)) {
            return loadBitmapFromStream(new FileInputStream("/sdcard/Pictures/src.jpg"));
        }else {
            return loadBitmapFromStream(new FileInputStream("/sdcard/Pictures/src.bmp"));
        }
    }

    private Bitmap loadDstBitmapFromLocalFile() throws FileNotFoundException {
        return loadBitmapFromStream(new FileInputStream("/sdcard/Pictures/dst.bmp"));
    }

    public void testScaleToFitCenter() throws Exception {
        Benchmark benchmark = new Benchmark();
        Bitmap srcBitmap1 = loadSrcBitmapFromLocalFile("bmp");
        benchmark.report("loadSrcBitmapFromLocalFile");
        Bitmap dstBitmap = loadDstBitmapFromLocalFile();
        benchmark.report("loadDstBitmapFromLocalFile");
        BitmapUtils.scaleToFitCenter(srcBitmap1, dstBitmap);
    }
}
