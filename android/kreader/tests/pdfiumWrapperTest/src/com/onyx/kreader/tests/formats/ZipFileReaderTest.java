package com.onyx.kreader.tests.formats;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.formats.model.zip.ZipFileReader;
import com.onyx.kreader.tests.ReaderTestActivity;

/**
 * Created by zengzhu on 3/17/16.
 */
public class ZipFileReaderTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private String TAG = TxtBookReaderTest.class.getSimpleName();

    public ZipFileReaderTest() {
        super(ReaderTestActivity.class);
    }

    public void testZipReader1() {
        final String path = "/mnt/sdcard/Books/test.epub";
        ZipFileReader zipFileReader = new ZipFileReader();
        assertTrue(zipFileReader.open(path, null));
        assertTrue(zipFileReader.entryCount() > 0);
        assertTrue(zipFileReader.close());
    }

    public void testZipReader2() {
        final String path = "/mnt/sdcard/Books/test.epub";
        ZipFileReader zipFileReader = new ZipFileReader();
        assertTrue(zipFileReader.open(path, null));
        assertTrue(zipFileReader.entryCount() > 0);
        assertTrue(zipFileReader.getEntry("META-INF/container.xml") != null);
        assertTrue(zipFileReader.getEntry("mimetype") != null);
        assertTrue(zipFileReader.getEntry("OPS/css/book.css") != null);
        assertTrue(zipFileReader.getEntry("OPS/images/image.png") != null);
        assertTrue(zipFileReader.getEntry("OPS/toc.xhtml") != null);
        assertTrue(zipFileReader.close());
    }

}
