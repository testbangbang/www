package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        FlowConfig.Builder builder = new FlowConfig.Builder(getContext());
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
    }


    public void testShapeDocumentOpen() {
        NoteDocument noteDocument = new NoteDocument();
        noteDocument.open(getContext(), UUID.randomUUID().toString(), null);
        final NotePage page = noteDocument.getPageByIndex(0);
        assertNotNull(page);
    }

    public void testShapeDocumentCreatePage() {
        NoteDocument noteDocument = new NoteDocument();
        noteDocument.open(getContext(), UUID.randomUUID().toString(), null);
        NotePage page = noteDocument.getPageByIndex(0);
        assertNotNull(page);
        noteDocument.createBlankPage(getContext(), 1);
        page = noteDocument.getPageByIndex(1);
        assertNotNull(page);
        noteDocument.removePage(getContext(), 1);
        page = noteDocument.getPageByIndex(1);
        assertNull(page);
        assertTrue(noteDocument.getCurrentPageIndex() == 0);
    }

    public void testShapeDocumentCreatePage2() {
        NoteDocument noteDocument = new NoteDocument();
        noteDocument.open(getContext(), UUID.randomUUID().toString(), null);

        // get original first page
        NotePage page = noteDocument.getPageByIndex(0);
        String oldFirstPageId = page.getPageUniqueId();

        // add new page
        noteDocument.createBlankPage(getContext(), 0);
        page = noteDocument.getPageByIndex(0);
        String newFirstPageId = page.getPageUniqueId();

        // origin first page should be page 1.
        page = noteDocument.getPageByIndex(1);
        assertTrue(page.getPageUniqueId().equalsIgnoreCase(oldFirstPageId));

        // new first page
        page = noteDocument.getPageByIndex(0);
        assertNotNull(page);
        assertTrue(newFirstPageId.equalsIgnoreCase(page.getPageUniqueId()));
    }

    public void testShapeDocumentCreatePage3() {
        NoteDocument noteDocument = new NoteDocument();
        noteDocument.open(getContext(), UUID.randomUUID().toString(), null);

        // get original first page
        NotePage page = noteDocument.getPageByIndex(0);
        String firstPageId = page.getPageUniqueId();

        // add new page
        noteDocument.createBlankPage(getContext(), 1);
        page = noteDocument.getPageByIndex(1);
        String secondPageId = page.getPageUniqueId();

        // origin first page should be page 1.
        page = noteDocument.getPageByIndex(0);
        assertTrue(page.getPageUniqueId().equalsIgnoreCase(firstPageId));

        // new first page
        page = noteDocument.getPageByIndex(1);
        assertNotNull(page);
        assertTrue(secondPageId.equalsIgnoreCase(page.getPageUniqueId()));
    }

    public void testShapeDocumentNavigation() {
        NoteDocument noteDocument = new NoteDocument();
        noteDocument.open(getContext(), UUID.randomUUID().toString(), null);

        List<String> pages = new ArrayList<String>();
        int max = TestUtils.randInt(10, 30);
        for(int i = 1; i < max; ++i) {
            noteDocument.createBlankPage(getContext(), i);
        }
        assertTrue(noteDocument.getPageCount() == max);
        for(int i = 0; i < max; ++i) {
            String id = noteDocument.getPage(i, null).getPageUniqueId();
            assertNotNull(id);
            pages.add(id);
        }
        noteDocument.gotoFirst();
        int index = noteDocument.getCurrentPageIndex();
        assertTrue(index == 0);
        NotePage notePage = noteDocument.getPageByIndex(index);
        assertTrue(notePage.getPageUniqueId().equalsIgnoreCase(pages.get(index)));

        for(int i = 1; i < max; ++i) {
            noteDocument.nextPage();
            index = noteDocument.getCurrentPageIndex();
            assertTrue(index == i);
            notePage = noteDocument.getPageByIndex(index);
            assertTrue(notePage.getPageUniqueId().equalsIgnoreCase(pages.get(index)));
        }

        noteDocument.gotoLast();
        index = noteDocument.getCurrentPageIndex();
        assertTrue(index == pages.size() - 1);
        notePage = noteDocument.getPageByIndex(index);
        assertTrue(notePage.getPageUniqueId().equalsIgnoreCase(pages.get(index)));

    }
}