package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.scribble.data.ShapeDocument;
import com.onyx.android.sdk.scribble.data.ShapePage;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.scribbleGeneratedDatabaseHolder;

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
        builder.addDatabaseHolder(scribbleGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
    }


    public void testShapeDocumentOpen() {
        ShapeDocument shapeDocument = new ShapeDocument();
        shapeDocument.open(getContext(), UUID.randomUUID().toString());
        final ShapePage page = shapeDocument.getPageByIndex(0);
        assertNotNull(page);
    }

    public void testShapeDocumentCreatePage() {
        ShapeDocument shapeDocument = new ShapeDocument();
        shapeDocument.open(getContext(), UUID.randomUUID().toString());
        ShapePage page = shapeDocument.getPageByIndex(0);
        assertNotNull(page);
        shapeDocument.createBlankPage(getContext(), 1);
        page = shapeDocument.getPageByIndex(1);
        assertNotNull(page);
        shapeDocument.removePage(getContext(), 1);
        page = shapeDocument.getPageByIndex(1);
        assertNull(page);
        assertTrue(shapeDocument.getCurrentPageIndex() == 0);
    }

    public void testShapeDocumentCreatePage2() {
        ShapeDocument shapeDocument = new ShapeDocument();
        shapeDocument.open(getContext(), UUID.randomUUID().toString());

        // get original first page
        ShapePage page = shapeDocument.getPageByIndex(0);
        String oldFirstPageId = page.getPageUniqueId();

        // add new page
        shapeDocument.createBlankPage(getContext(), 0);
        page = shapeDocument.getPageByIndex(0);
        String newFirstPageId = page.getPageUniqueId();

        // origin first page should be page 1.
        page = shapeDocument.getPageByIndex(1);
        assertTrue(page.getPageUniqueId().equalsIgnoreCase(oldFirstPageId));

        // new first page
        page = shapeDocument.getPageByIndex(0);
        assertNotNull(page);
        assertTrue(newFirstPageId.equalsIgnoreCase(page.getPageUniqueId()));
    }

    public void testShapeDocumentCreatePage3() {
        ShapeDocument shapeDocument = new ShapeDocument();
        shapeDocument.open(getContext(), UUID.randomUUID().toString());

        // get original first page
        ShapePage page = shapeDocument.getPageByIndex(0);
        String firstPageId = page.getPageUniqueId();

        // add new page
        shapeDocument.createBlankPage(getContext(), 1);
        page = shapeDocument.getPageByIndex(1);
        String secondPageId = page.getPageUniqueId();

        // origin first page should be page 1.
        page = shapeDocument.getPageByIndex(0);
        assertTrue(page.getPageUniqueId().equalsIgnoreCase(firstPageId));

        // new first page
        page = shapeDocument.getPageByIndex(1);
        assertNotNull(page);
        assertTrue(secondPageId.equalsIgnoreCase(page.getPageUniqueId()));
    }

    public void testShapeDocumentNavigation() {
        ShapeDocument shapeDocument = new ShapeDocument();
        shapeDocument.open(getContext(), UUID.randomUUID().toString());

        List<String> pages = new ArrayList<String>();
        int max = TestUtils.randInt(10, 30);
        for(int i = 1; i < max; ++i) {
            shapeDocument.createBlankPage(getContext(), i);
        }
        assertTrue(shapeDocument.getPageCount() == max);
        for(int i = 0; i < max; ++i) {
            String id = shapeDocument.getPage(i, null).getPageUniqueId();
            assertNotNull(id);
            pages.add(id);
        }
        shapeDocument.gotoFirst();
        int index = shapeDocument.getCurrentPageIndex();
        assertTrue(index == 0);
        ShapePage shapePage = shapeDocument.getPageByIndex(index);
        assertTrue(shapePage.getPageUniqueId().equalsIgnoreCase(pages.get(index)));

        for(int i = 1; i < max; ++i) {
            shapeDocument.nextPage();
            index = shapeDocument.getCurrentPageIndex();
            assertTrue(index == i);
            shapePage = shapeDocument.getPageByIndex(index);
            assertTrue(shapePage.getPageUniqueId().equalsIgnoreCase(pages.get(index)));
        }

        shapeDocument.gotoLast();
        index = shapeDocument.getCurrentPageIndex();
        assertTrue(index == pages.size() - 1);
        shapePage = shapeDocument.getPageByIndex(index);
        assertTrue(shapePage.getPageUniqueId().equalsIgnoreCase(pages.get(index)));

    }
}