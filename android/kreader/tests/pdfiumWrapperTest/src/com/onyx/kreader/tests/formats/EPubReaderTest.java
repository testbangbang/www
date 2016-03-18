package com.onyx.kreader.tests.formats;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.formats.epub.EPubBookReader;
import com.onyx.kreader.formats.model.BookMetadata;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReaderContext;
import com.onyx.kreader.formats.model.zip.ZipFileReader;
import com.onyx.kreader.tests.ReaderTestActivity;

/**
 * Created by zengzhu on 3/17/16.
 */
public class EPubReaderTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private String TAG = TxtBookReaderTest.class.getSimpleName();

    public EPubReaderTest() {
        super(ReaderTestActivity.class);
    }

    public void testEPubReader1() {
        final String path = "/mnt/sdcard/Books/test.epub";
        BookModel bookModel = new BookModel();
        BookReaderContext context = new BookReaderContext();
        context.path = path;
        EPubBookReader zipFileReader = new EPubBookReader();
        assertTrue(zipFileReader.open(context, bookModel));
        assertTrue(zipFileReader.close(null));
    }

}
