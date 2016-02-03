package com.onyx.reader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.reader.plugins.pdfium.PdfiumJniWrapper;
import com.onyx.reader.test.ReaderTestActivity;

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

    public void testOpen() {
        PdfiumJniWrapper wrapper = new PdfiumJniWrapper();
        assertTrue(wrapper.nativeInitLibrary());
        assertTrue(wrapper.nativeOpenDocument("/mnt/sdcard/Books/a.pdf", null) == 0);
        assertTrue(wrapper.nativeCloseDocument());
    }

}
