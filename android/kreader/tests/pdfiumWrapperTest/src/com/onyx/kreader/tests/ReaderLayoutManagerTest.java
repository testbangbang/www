package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.host.layout.LayoutManagerCallback;
import com.onyx.kreader.host.layout.LayoutProvider;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.TestUtils;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ReaderLayoutManagerTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderLayoutManagerTest() {
        super(ReaderTestActivity.class);
    }

    public void testSinglePage() throws Exception  {
        FakeReader reader = new FakeReader();
        reader.open();
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(reader,
                reader,
                reader,
                reader);

        layoutManager.init();
        layoutManager.updateViewportSize();
        assertTrue(layoutManager.setCurrentLayout(ReaderConstants.SINGLE_PAGE));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(ReaderConstants.SINGLE_PAGE));

        String position = reader.getInitPosition();
        assertNotNull(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(position));

        position = reader.getNavigator().nextPage(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(position));

        position = reader.getNavigator().prevPage(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(position));

        int total = reader.getTotalPage();
        position = reader.getNavigator().getPositionByPageNumber(total / 2);
        assertNotNull(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(position));
    }

    /**
     * test with scale to page
     * @throws Exception
     */
    public void testSinglePageSnapshot1() throws Exception  {
        FakeReader reader = new FakeReader();
        reader.open();
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(reader,
                reader,
                reader,
                reader);

        layoutManager.init();
        layoutManager.updateViewportSize();
        assertTrue(layoutManager.setCurrentLayout(ReaderConstants.SINGLE_PAGE));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(ReaderConstants.SINGLE_PAGE));
        assertFalse(layoutManager.canGoBack());
        assertFalse(layoutManager.canGoForward());

        // before position.  page 0, current is 0
        String position = reader.getInitPosition();
        assertNotNull(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(position));
        assertFalse(layoutManager.canGoBack());
        final PageInfo before = layoutManager.getCurrentPageInfo();
        assertNotNull(before);

        // goto new position. goto page 1, page 0 is stored in history, current is 1
        String nextPage = reader.getNavigator().nextPage(position);
        assertTrue(layoutManager.gotoPosition(nextPage));
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(nextPage));
        final PageInfo newBefore = layoutManager.getCurrentPageInfo();

        // go back to before. goto page 0, page 1 is stored in history, current is 0
        assertTrue(layoutManager.canGoBack());
        assertTrue(layoutManager.goBack());
        final PageInfo after = layoutManager.getCurrentPageInfo();
        String newPosition = layoutManager.getCurrentPageName();
        assertTrue(newPosition.equalsIgnoreCase(position));
        assertTrue(JSON.toJSONString(before).equalsIgnoreCase(JSON.toJSONString(after)));

        // forward. goto page 1 now, page 0 is stored in history, current is 1
        assertTrue(layoutManager.canGoForward());
        assertTrue(layoutManager.goForward());
        final PageInfo newAfter = layoutManager.getCurrentPageInfo();
        String newNextPosition = layoutManager.getCurrentPageName();
        assertTrue(newNextPosition.equalsIgnoreCase(nextPage));
        assertTrue(JSON.toJSONString(newBefore).equalsIgnoreCase(JSON.toJSONString(newAfter)));

    }

    /**
     * test with scale and viewport.
     * @throws Exception
     */
    public void testSinglePageSnapshot2() throws Exception  {
        FakeReader reader = new FakeReader();
        reader.open();
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(reader,
                reader,
                reader,
                reader);

        layoutManager.init();
        layoutManager.updateViewportSize();
        assertTrue(layoutManager.setCurrentLayout(ReaderConstants.SINGLE_PAGE));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(ReaderConstants.SINGLE_PAGE));
        assertFalse(layoutManager.canGoBack());
        assertFalse(layoutManager.canGoForward());

        // before position.  page 0, current is 0
        String position = reader.getInitPosition();
        assertNotNull(position);
        float scale = TestUtils.randInt(1, 5);
        float x = TestUtils.randInt(10, 50);
        float y = TestUtils.randInt(10, 50);
        layoutManager.setScale(position, scale, x, y);
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(position));
        assertFalse(layoutManager.canGoBack());
        final PageInfo before = layoutManager.getCurrentPageInfo();
        assertNotNull(before);

        // goto new position. goto page 1, page 0 is stored in history, current is 1
        String nextPage = reader.getNavigator().nextPage(position);
        assertTrue(layoutManager.gotoPosition(nextPage));
        assertTrue(layoutManager.getCurrentPageName().equalsIgnoreCase(nextPage));
        final PageInfo newBefore = layoutManager.getCurrentPageInfo();

        // go back to before. goto page 0, page 1 is stored in history, current is 0
        assertTrue(layoutManager.canGoBack());
        assertTrue(layoutManager.goBack());
        final PageInfo after = layoutManager.getCurrentPageInfo();
        String newPosition = layoutManager.getCurrentPageName();
        assertTrue(newPosition.equalsIgnoreCase(position));
        assertTrue(JSON.toJSONString(before).equalsIgnoreCase(JSON.toJSONString(after)));

        // forward. goto page 1 now, page 0 is stored in history, current is 1
        assertTrue(layoutManager.canGoForward());
        assertTrue(layoutManager.goForward());
        final PageInfo newAfter = layoutManager.getCurrentPageInfo();
        String newNextPosition = layoutManager.getCurrentPageName();
        assertTrue(newNextPosition.equalsIgnoreCase(nextPage));
        assertTrue(JSON.toJSONString(newBefore).equalsIgnoreCase(JSON.toJSONString(newAfter)));
    }

}
