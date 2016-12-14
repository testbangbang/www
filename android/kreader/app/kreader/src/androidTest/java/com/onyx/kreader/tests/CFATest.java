package com.onyx.kreader.tests;

import android.graphics.Bitmap;
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

    public void testRender() throws Exception {
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
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));

        // to grayscale
        BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/Books/argb.png");

        int strideInBytes = bitmap.getWidth() * 4;
        final byte [] data = new byte[strideInBytes * bitmap.getHeight()];
        ImageUtils.toGrayScale(bitmap, data, strideInBytes);

        // render origin image rect here.
        // copy rect of source bitmap to dst bitmap with cfa applied.
        final byte [] rgbw = new byte[strideInBytes * bitmap.getHeight() * 4];
        ImageUtils.toRgbw(bitmap, 0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1, rgbw, strideInBytes * 2);
        FileUtils.saveContentToFile(rgbw, new File("/mnt/sdcard/Books/rgbw.bin"));

        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }


}
