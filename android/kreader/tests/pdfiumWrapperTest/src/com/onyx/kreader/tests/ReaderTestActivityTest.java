package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.plugins.pdfium.PdfiumJniWrapper;
import com.onyx.kreader.test.ReaderTestActivity;
import com.onyx.kreader.utils.StringUtils;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class ReaderTestActivityTest \
 * com.onyx.reader.tests/android.test.InstrumentationTestRunner
 */
public class ReaderTestActivityTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderTestActivityTest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void testOpen() throws Exception {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/b.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        String title = wrapper.metadataString("Title");
        assertTrue(StringUtils.isNonBlank(title));
        assertTrue(wrapper.nativeCloseDocument());
        assertFalse(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testOpenWithPassword() throws Exception {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/d.pdf", null) == PdfiumJniWrapper.ERROR_PASSWORD_INVALID);
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/d.pdf", "12456") == PdfiumJniWrapper.ERROR_PASSWORD_INVALID);
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/d.pdf", "123456") == PdfiumJniWrapper.NO_ERROR);
        assertTrue(wrapper.nativeCloseDocument());
        assertFalse(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageCount() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/a.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        assertTrue(wrapper.nativePageCount() > 10);
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageRender() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/a.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        int pageCount = wrapper.nativePageCount();
        assertTrue(pageCount > 0);
        int page = pageCount - 1;
        float size[] = new float[2];
        assertTrue(wrapper.nativePageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmap));
        assertFalse(wrapper.nativePageSize(page + 1, size));
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageTextSelection() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/c.pdf", null) == PdfiumJniWrapper.NO_ERROR);
        int pageCount = wrapper.nativePageCount();
        assertTrue(pageCount > 0);
        int page = pageCount - 1;
        float size[] = new float[2];
        assertTrue(wrapper.nativePageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.drawPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmap));
        assertFalse(wrapper.nativePageSize(page + 1, size));
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

}
