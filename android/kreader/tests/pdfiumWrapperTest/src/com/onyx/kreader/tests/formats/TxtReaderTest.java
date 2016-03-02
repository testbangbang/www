package com.onyx.kreader.tests.formats;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.ParagraphEntry;
import com.onyx.kreader.formats.model.TextParagraphEntry;
import com.onyx.kreader.formats.txt.TxtReader;
import com.onyx.kreader.tests.ReaderTestActivity;

import java.util.List;

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
        for(int i = 0; i < 3; ++i) {
            assertTrue(reader.open(bookModel));
            int round = 0;
            while (true) {
                if (!reader.processNext(bookModel)) {
                    break;
                }
                assertNotNull(bookModel.getTextModel().getLastParagraph());
                final List<ParagraphEntry> entryList = bookModel.getTextModel().getLastParagraph().getParagraphEntryList();
                assertNotNull(entryList);
                ParagraphEntry entry = entryList.get(0);
                assertNotNull(entry);
                assertTrue(entry.getEntryKind() == TextParagraphEntry.EntryKind.TEXT_ENTRY);
                assertTrue(entry instanceof TextParagraphEntry);

                TextParagraphEntry textParagraphEntry = (TextParagraphEntry)entry;
                assertTrue(textParagraphEntry.getText().split("\n").length <= 1);

                ++round;
            }
            assertTrue(bookModel.getModelHelper().getEncoding().equalsIgnoreCase("GB18030"));
            assertTrue(reader.close(bookModel));
        }
    }

}
