package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.plugins.pdfium.PdfiumJniWrapper;
import com.onyx.kreader.utils.StringUtils;

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
public class ReaderPluginPdfiumTest2 extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    static private String TAG = ReaderPluginPdfiumTest2.class.getSimpleName();
    private boolean performanceTest = false;

    public ReaderPluginPdfiumTest2() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void testOpen() throws Exception {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/normal.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        String title = wrapper.metadataString("Title");
        assertTrue(StringUtils.isNotBlank(title));
        assertTrue(wrapper.nativeCloseDocument());
        assertFalse(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testOpenWithPassword() throws Exception {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/password.pdf", null) == PdfiumJniWrapper.ERROR_PASSWORD_INVALID);
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/password.pdf", "12456") == PdfiumJniWrapper.ERROR_PASSWORD_INVALID);
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/password.pdf", "123456") == PdfiumJniWrapper.NO_ERROR);
        assertTrue(wrapper.nativeCloseDocument());
        assertFalse(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageCount() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/normal.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        assertTrue(wrapper.nativePageCount() > 10);
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageRender() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/normal.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        int pageCount = wrapper.nativePageCount();
        assertTrue(pageCount > 0);
        int page = pageCount - 1;
        float size[] = new float[2];
        assertTrue(wrapper.nativePageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
        assertFalse(wrapper.nativePageSize(page + 1, size));
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageTextSelection() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/normal.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        int pageCount = wrapper.nativePageCount();
        assertTrue(pageCount > 0);
        int page = pageCount - 1;
        float size[] = new float[2];
        assertTrue(wrapper.nativePageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0, bitmap));
        assertFalse(wrapper.nativePageSize(page + 1, size));
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageRenderPerformance() {
        if (!performanceTest) {
            return;
        }
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        String [] pathes = { "/mnt/sdcard/cityhunter/001.pdf",
                "/mnt/sdcard/cityhunter/002.pdf",
                "/mnt/sdcard/cityhunter/003.pdf",
                "/mnt/sdcard/cityhunter/004.pdf",
                "/mnt/sdcard/cityhunter/005.pdf",
                "/mnt/sdcard/cityhunter/006.pdf",
                "/mnt/sdcard/cityhunter/007.pdf"};
        for(String path : pathes) {
            assertTrue(wrapper.nativeInitLibrary());
            assertTrue(wrapper.nativeOpenDocument(path, null) == PdfiumJniWrapper.NO_ERROR);
            int pageCount = wrapper.nativePageCount();
            assertTrue(pageCount > 0);
            ReaderBitmapImpl readerBitmap = null;
            for (int i = 0; i < pageCount; ++i) {
                float size[] = new float[2];
                assertTrue(wrapper.nativePageSize(i, size));
                assertTrue(size[0] > 0);
                assertTrue(size[1] > 0);
                if (readerBitmap == null) {
                    readerBitmap = new ReaderBitmapImpl((int) size[0], (int) size[1], Bitmap.Config.ARGB_8888);
                } else {
                    readerBitmap.update((int) size[0], (int) size[1], Bitmap.Config.ARGB_8888);
                }
                Bitmap bitmap = readerBitmap.getBitmap();
                long start = System.currentTimeMillis();
                assertTrue(wrapper.drawPage(i, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0,  bitmap));
                long end = System.currentTimeMillis();
                Log.i(TAG, "Performance testing: " + path +  " page: " + i + " ts: " + (end - start));
            }
            readerBitmap.recycleBitmap();
            assertTrue(wrapper.nativeCloseDocument());
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
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        String path = "/mnt/sdcard/Books/text.pdf";
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument(path, null) == PdfiumJniWrapper.NO_ERROR);
        String text = wrapper.getPageText(0);
        assertTrue(text.contains("广州"));
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testSearch() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        String path = "/mnt/sdcard/Books/text.pdf";
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument(path, null) == PdfiumJniWrapper.NO_ERROR);
        String pattern = "广州";
        List<ReaderSelection> list = new ArrayList<ReaderSelection>();
        wrapper.searchInPage(0, 0, 0, 1024, 768, 0, pattern, false, true, list);
        assertTrue(list.size() > 0);
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }



}
