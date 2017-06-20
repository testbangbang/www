package com.onyx.kreader.tests;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.AlBookProperties;
import com.neverland.engbook.forpublic.AlOneContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.plugins.alreader.AlReaderWrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by joy on 12/16/16.
 */

public class ReaderPluginAlReaderTest extends ApplicationTestCase<Application> {

    public ReaderPluginAlReaderTest() {
        super(Application.class);
    }

    public void testTableOfContent() throws Exception {
        AlReaderWrapper wrapper = new AlReaderWrapper(getContext(),
                ReaderPluginOptionsImpl.create(getContext()));
        assertEquals(wrapper.openDocument("/extsd/Books/盗墓笔记（插图版）.epub", null), wrapper.NO_ERROR);

        Field fieldBookEng = wrapper.getClass().getDeclaredField("bookEng");
        assertNotNull(fieldBookEng);
        fieldBookEng.setAccessible(true);
        AlBookEng bookEng = (AlBookEng) fieldBookEng.get(wrapper);
        assertNotNull(bookEng);

        AlBookProperties properties = bookEng.getBookProperties(true);
        assertNotNull(properties.content);

        ReaderDocumentTableOfContent toc = new ReaderDocumentTableOfContent();
        assertTrue(wrapper.readTableOfContent(toc));

        assertEquals(countTableOfContentEntries(properties.content),
                countTableOfContentEntries(toc.getRootEntry()));
    }

    private int countTableOfContentEntries(ArrayList<AlOneContent> contents) {
        int count = 0;
        for (AlOneContent content : contents) {
            if (!content.isBookmark) {
                count++;
            }
        }
        return count;
    }

    private int countTableOfContentEntries(ReaderDocumentTableOfContentEntry root) {
        int count = 0;
        if (root.getChildren() == null) {
            return 0;
        }
        for (ReaderDocumentTableOfContentEntry entry : root.getChildren()) {
            count += countTableOfContentEntries(entry);
            count++;
        }
        return count;
    }

}
