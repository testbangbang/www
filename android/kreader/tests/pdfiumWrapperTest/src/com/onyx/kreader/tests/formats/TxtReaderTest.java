package com.onyx.kreader.tests.formats;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookContext;
import com.onyx.kreader.formats.txt.TxtReader;
import com.onyx.kreader.tests.ReaderTestActivity;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TxtReaderTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private String TAG = TxtReaderTest.class.getSimpleName();

    public TxtReaderTest() {
        super(ReaderTestActivity.class);
    }

    public void testTxtReader() {
        final String path = "/mnt/sdcard/Books/test.txt";
        TxtReader reader = new TxtReader();
        BookModel bookModel = new BookModel(path);
        assertTrue(reader.open(bookModel));
        int round = 0;
        while (reader.processNext(bookModel)) {
            Log.d(TAG, "round: " + round);
            ++round;
        }
        assertTrue(bookModel.getBookContext().encoding.equalsIgnoreCase("GB18030"));
    }

}
