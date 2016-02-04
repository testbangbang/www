package com.onyx.reader.tests;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.reader.plugins.pdfium.PdfiumJniWrapper;
import com.onyx.reader.test.ReaderTestActivity;
import com.onyx.reader.utils.StringUtils;

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
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/b.pdf", null) == 0);
        String title = wrapper.metadataString("Title");
        assertTrue(StringUtils.isNonBlank(title));
        assertTrue(wrapper.nativeCloseDocument());
        assertFalse(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageCount() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/a.pdf", null) == 0);
        assertTrue(wrapper.nativePageCount() > 10);
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

    public void testPageRender() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/a.pdf", null) == 0);
        int pageCount = wrapper.nativePageCount();
        assertTrue(pageCount > 0);
        int page = pageCount - 1;
        float size[] = new float[2];
        assertTrue(wrapper.nativePageSize(page, size));
        assertTrue(size[0] > 0);
        assertTrue(size[1] > 0);
        Bitmap bitmap = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.ARGB_8888);
        assertTrue(wrapper.nativeRenderPage(page, 0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmap));
        assertFalse(wrapper.nativePageSize(page + 1, size));
        assertTrue(wrapper.nativeCloseDocument());
        assertTrue(wrapper.nativeDestroyLibrary());
    }

}
