package com.onyx.android.sdk.reader.tests;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.AlBookProperties;
import com.neverland.engbook.forpublic.AlOneContent;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.plugins.alreader.AlReaderWrapper;
import com.onyx.android.sdk.utils.LocaleUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 12/16/16.
 */

public class ReaderPluginAlReaderJEBTest extends ApplicationTestCase<Application> {

    public ReaderPluginAlReaderJEBTest() {
        super(Application.class);
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

    public void testJEBBook() throws Exception {
        List<String> bookPaths = new ArrayList<>();

        bookPaths.add("/mnt/sdcard/JDBooks/6db5d0f8c554138510d18be8b9aa03af.JEB");
        bookPaths.add("/mnt/sdcard/JDBooks/b9d336328ee54215392ee9cca6f171bd.JEB");

        for(int i = 0;i < bookPaths.size();i++) {
            AlReaderWrapper wrapper = new AlReaderWrapper(getContext(),
                    ReaderPluginOptionsImpl.create(getContext()));


            final ReaderDocumentOptionsImpl documentOptions = new ReaderDocumentOptionsImpl(null,
                    null,
                    0,
                    LocaleUtils.getLocaleDefaultCodePage(),
                    ReaderChineseConvertType.NONE,
                    false);

            assertEquals(wrapper.openDocument(bookPaths.get(i), documentOptions), wrapper.NO_ERROR);

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
    }
}
