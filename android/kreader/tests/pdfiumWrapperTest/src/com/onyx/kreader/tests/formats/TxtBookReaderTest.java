package com.onyx.kreader.tests.formats;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReaderContext;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;
import com.onyx.kreader.formats.txt.TxtBookReader;
import com.onyx.kreader.tests.ReaderTestActivity;

import java.util.List;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TxtBookReaderTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private String TAG = TxtBookReaderTest.class.getSimpleName();

    public TxtBookReaderTest() {
        super(ReaderTestActivity.class);
    }

    public void testTxtReader1() {
        final String path = "/mnt/sdcard/Books/test.txt";
        BookReaderContext context = new BookReaderContext();
        context.path = path;
        TxtBookReader reader = new TxtBookReader();
        BookModel bookModel = new BookModel();
        for(int i = 0; i < 2; ++i) {
            assertTrue(reader.open(context, bookModel));
            assertFalse(bookModel.getTextModel().isLoadFinished());
            while (true) {
                if (!reader.processNext(bookModel)) {
                    break;
                }
                assertNotNull(bookModel.getTextModel().getLastParagraph());
                final List<ParagraphEntry> entryList = bookModel.getTextModel().getLastParagraph().getEntryList();
                assertNotNull(entryList);
                ParagraphEntry entry = entryList.get(0);
                assertNotNull(entry);
                assertTrue(entry.getEntryKind() == TextParagraphEntry.EntryKind.TEXT_ENTRY);
                assertTrue(entry instanceof TextParagraphEntry);

                TextParagraphEntry textParagraphEntry = (TextParagraphEntry)entry;
                assertTrue(textParagraphEntry.getText().split("\n").length <= 1);
            }
            final Paragraph paragraph = bookModel.getTextModel().getLastParagraph();
            assertNotNull(paragraph);
            assertTrue(bookModel.getTextModel().paragraphCount() == 51373);
            assertTrue(bookModel.getTextModel().isLoadFinished());
            assertTrue(reader.getFileReader().getEncoding().equalsIgnoreCase("GB18030"));
            assertTrue(reader.close(bookModel));
        }
    }

    public void testTxtReader2() {
        final String path = "/mnt/sdcard/Books/test2.txt";
        BookReaderContext context = new BookReaderContext();
        context.path = path;
        TxtBookReader reader = new TxtBookReader();
        BookModel bookModel = new BookModel();
        for(int i = 0; i < 2; ++i) {
            assertTrue(reader.open(context, bookModel));
            while (true) {
                if (!reader.processNext(bookModel)) {
                    break;
                }
                assertNotNull(bookModel.getTextModel().getLastParagraph());
                final List<ParagraphEntry> entryList = bookModel.getTextModel().getLastParagraph().getEntryList();
                assertNotNull(entryList);
                ParagraphEntry entry = entryList.get(0);
                assertNotNull(entry);
                assertTrue(entry.getEntryKind() == TextParagraphEntry.EntryKind.TEXT_ENTRY);
                assertTrue(entry instanceof TextParagraphEntry);

                TextParagraphEntry textParagraphEntry = (TextParagraphEntry)entry;
                assertTrue(textParagraphEntry.getText().split("\n").length <= 1);
            }
            assertTrue(reader.getFileReader().getEncoding().equalsIgnoreCase("GB18030"));
            assertTrue(reader.close(bookModel));
        }
    }

}
