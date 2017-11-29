package com.onyx.android.sdk.reader.tests;

import android.app.Application;
import android.graphics.Bitmap;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.plugins.neopdf.NeoPdfJniWrapper;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class ReaderPluginPdfiumTest2 \
 * com.onyx.reader.tests/android.test.InstrumentationTestRunner
 */
public class ReaderPluginPdfiumTest2 extends ApplicationTestCase<Application> {

    static private String TAG = ReaderPluginPdfiumTest2.class.getSimpleName();
    private boolean performanceTest = false;

    public ReaderPluginPdfiumTest2() {
        super(Application.class);
        Debug.setDebug(true);
    }

    public void testOpen() throws Exception {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/normal.pdf", null) == NeoPdfJniWrapper.NO_ERROR);
        String title = wrapper.metadataString("Title");
        assertTrue(StringUtils.isNotBlank(title));
        assertTrue(wrapper.closeDocument());
        assertFalse(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testOpenWithPassword() throws Exception {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/password.pdf", null) == NeoPdfJniWrapper.ERROR_PASSWORD_INVALID);
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/password.pdf", "12456") == NeoPdfJniWrapper.ERROR_PASSWORD_INVALID);
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/password.pdf", "123456") == NeoPdfJniWrapper.NO_ERROR);
        assertTrue(wrapper.closeDocument());
        assertFalse(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageCount() {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/normal.pdf", null) == NeoPdfJniWrapper.NO_ERROR);
        assertTrue(wrapper.pageCount() > 10);
        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageRender() {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/normal.pdf", null) == NeoPdfJniWrapper.NO_ERROR);
        int pageCount = wrapper.pageCount();
        assertTrue(pageCount > 0);
        int page = pageCount - 1;
        float size[] = new float[2];
        assertTrue(wrapper.pageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
        assertFalse(wrapper.pageSize(page + 1, size));
        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testImageRender() {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument("/sdcard/Books/longzhu.pdf", null) == NeoPdfJniWrapper.NO_ERROR);
        int pageCount = wrapper.pageCount();
        assertTrue(pageCount > 0);

        Benchmark benchmark = new Benchmark();
        wrapper.setUsingHighQualityImageRenderer(false);
        for (int page = 0; page < 10 && page < pageCount - 1; page++) {
            float size[] = new float[2];
            assertTrue(wrapper.pageSize(page, size));
            assertTrue(size[0] > 0);
            assertTrue(size[1] > 0);
            Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
            assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
            bitmap.recycle();
        }
        benchmark.report("draw without using high quality image renderer");

        benchmark.restart();
        wrapper.setUsingHighQualityImageRenderer(true);
        for (int page = 0; page < 10 && page < pageCount - 1; page++) {
            float size[] = new float[2];
            assertTrue(wrapper.pageSize(page, size));
            assertTrue(size[0] > 0);
            assertTrue(size[1] > 0);
            Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
            assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
            bitmap.recycle();
        }
        benchmark.report("draw using high quality image renderer");

        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageTextSelection() {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument("/mnt/sdcard/Books/normal.pdf", null) == NeoPdfJniWrapper.NO_ERROR);
        int pageCount = wrapper.pageCount();
        assertTrue(pageCount > 0);
        int page = pageCount - 1;
        float size[] = new float[2];
        assertTrue(wrapper.pageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
        assertFalse(wrapper.pageSize(page + 1, size));
        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageRenderPerformance() {
        if (!performanceTest) {
            return;
        }
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        String [] pathes = { "/mnt/sdcard/cityhunter/001.pdf",
                "/mnt/sdcard/cityhunter/002.pdf",
                "/mnt/sdcard/cityhunter/003.pdf",
                "/mnt/sdcard/cityhunter/004.pdf",
                "/mnt/sdcard/cityhunter/005.pdf",
                "/mnt/sdcard/cityhunter/006.pdf",
                "/mnt/sdcard/cityhunter/007.pdf"};
        for(String path : pathes) {
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
                    readerBitmap = new ReaderBitmapImpl((int) size[0], (int) size[1], Bitmap.Config.ARGB_8888);
                } else {
                    readerBitmap.update((int) size[0], (int) size[1], Bitmap.Config.ARGB_8888);
                }
                Bitmap bitmap = readerBitmap.getBitmap();
                long start = System.currentTimeMillis();
                assertTrue(wrapper.drawPage(i, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
                long end = System.currentTimeMillis();
                Log.i(TAG, "Performance testing: " + path +  " page: " + i + " ts: " + (end - start));
            }
            readerBitmap.recycleBitmap();
            assertTrue(wrapper.closeDocument());
            assertTrue(wrapper.nativeDestroyLibrary());
        }
    }

    public void testStringEncoding() {
        String pattern = "广州";
        byte [] buffer = StringUtils.utf16leBuffer(pattern);
        String result = StringUtils.utf16le(buffer);
        assertTrue(result.equalsIgnoreCase(pattern));
    }

    public void testPageText() {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        String path = "/mnt/sdcard/Books/text.pdf";
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument(path, null) == NeoPdfJniWrapper.NO_ERROR);
        String text = wrapper.getPageText(0);
        assertTrue(text.contains("广州"));
        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testSearch() {
        NeoPdfJniWrapper wrapper = new NeoPdfJniWrapper();
        String path = "/mnt/sdcard/Books/西游记.pdf";
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.openDocument(path, null) == NeoPdfJniWrapper.NO_ERROR);
//        String pattern = "上卷";
        String pattern = "邵康节";
        List<ReaderSelection> list = new ArrayList<ReaderSelection>();
        wrapper.searchInPage(1, 0, 0, 1024, 768, 0, pattern, false, false, 50, list);
        assertTrue(list.size() > 0);
        for(ReaderSelection selection : list) {
            assertTrue(selection.getText().equals(pattern));
            Log.d(TAG, JSON.toJSONString(selection));
        }
        assertTrue(wrapper.closeDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }



}
