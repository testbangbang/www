package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.android.sdk.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.android.sdk.reader.host.layout.ReaderLayoutManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.wrapper.ReaderHelper;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ReaderLayoutManagerTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private NavigationArgs navigationArgs;

    public ReaderLayoutManagerTest() {
        super(ReaderTestActivity.class);
    }


    public void testSinglePage() throws Exception  {
        FakeReader reader = new FakeReader();
        reader.open();
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(new ReaderHelper(),
                reader, reader, reader, reader, new ReaderViewOptionsImpl());
        layoutManager.init();
        layoutManager.updateViewportSize();
        assertTrue(layoutManager.setCurrentLayout(PageConstants.SINGLE_PAGE, navigationArgs));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(PageConstants.SINGLE_PAGE));

        String position = reader.getInitPosition();
        assertNotNull(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(position));

        position = reader.getNavigator().nextPage(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(position));

        position = reader.getNavigator().prevPage(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(position));

        int total = reader.getTotalPage();
        position = reader.getNavigator().getPositionByPageNumber(total / 2);
        assertNotNull(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(position));
    }

    /**
     * test with scale to page
     * @throws Exception
     */
    public void testSinglePageSnapshot1() throws Exception  {
        FakeReader reader = new FakeReader();
        reader.open();
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(new ReaderHelper(),
                reader, reader, reader, reader, new ReaderViewOptionsImpl());

        layoutManager.init();
        layoutManager.updateViewportSize();
        assertTrue(layoutManager.setCurrentLayout(PageConstants.SINGLE_PAGE, navigationArgs));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(PageConstants.SINGLE_PAGE));
        assertFalse(layoutManager.canGoBack());
        assertFalse(layoutManager.canGoForward());

        // before position.  page 0, current is 0
        String position = reader.getInitPosition();
        assertNotNull(position);
        assertTrue(layoutManager.gotoPosition(position));
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(position));
        assertFalse(layoutManager.canGoBack());
        final PageInfo before = layoutManager.getCurrentPageInfo();
        assertNotNull(before);

        // goto new position. goto page 1, page 0 is stored in history, current is 1
        String nextPage = reader.getNavigator().nextPage(position);
        assertTrue(layoutManager.gotoPosition(nextPage));
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(nextPage));
        final PageInfo newBefore = layoutManager.getCurrentPageInfo();

        // go back to before. goto page 0, page 1 is stored in history, current is 0
        assertTrue(layoutManager.canGoBack());
        assertTrue(layoutManager.goBack());
        final PageInfo after = layoutManager.getCurrentPageInfo();
        String newPosition = layoutManager.getCurrentPagePosition();
        assertTrue(newPosition.equalsIgnoreCase(position));
        assertTrue(JSON.toJSONString(before).equalsIgnoreCase(JSON.toJSONString(after)));

        // forward. goto page 1 now, page 0 is stored in history, current is 1
        assertTrue(layoutManager.canGoForward());
        assertTrue(layoutManager.goForward());
        final PageInfo newAfter = layoutManager.getCurrentPageInfo();
        String newNextPosition = layoutManager.getCurrentPagePosition();
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
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(new ReaderHelper(),
                reader, reader, reader, reader, new ReaderViewOptionsImpl());

        layoutManager.init();
        layoutManager.updateViewportSize();
        assertTrue(layoutManager.setCurrentLayout(PageConstants.SINGLE_PAGE, navigationArgs));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(PageConstants.SINGLE_PAGE));
        assertFalse(layoutManager.canGoBack());
        assertFalse(layoutManager.canGoForward());

        // before position.  page 0, current is 0
        String position = reader.getInitPosition();
        assertNotNull(position);
        float scale = TestUtils.randInt(1, 5);
        float x = TestUtils.randInt(10, 50);
        float y = TestUtils.randInt(10, 50);
        layoutManager.setScale(position, scale, x, y);
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(position));
        assertFalse(layoutManager.canGoBack());
        final PageInfo before = layoutManager.getCurrentPageInfo();
        assertNotNull(before);

        // goto new position. goto page 1, page 0 is stored in history, current is 1
        String nextPage = reader.getNavigator().nextPage(position);
        assertTrue(layoutManager.gotoPosition(nextPage));
        assertTrue(layoutManager.getCurrentPagePosition().equalsIgnoreCase(nextPage));
        final PageInfo newBefore = layoutManager.getCurrentPageInfo();

        // go back to before. goto page 0, page 1 is stored in history, current is 0
        assertTrue(layoutManager.canGoBack());
        assertTrue(layoutManager.goBack());
        final PageInfo after = layoutManager.getCurrentPageInfo();
        String newPosition = layoutManager.getCurrentPagePosition();
        assertTrue(newPosition.equalsIgnoreCase(position));
        assertTrue(JSON.toJSONString(before).equalsIgnoreCase(JSON.toJSONString(after)));

        // forward. goto page 1 now, page 0 is stored in history, current is 1
        assertTrue(layoutManager.canGoForward());
        assertTrue(layoutManager.goForward());
        final PageInfo newAfter = layoutManager.getCurrentPageInfo();
        String newNextPosition = layoutManager.getCurrentPagePosition();
        assertTrue(newNextPosition.equalsIgnoreCase(nextPage));
        assertTrue(JSON.toJSONString(newBefore).equalsIgnoreCase(JSON.toJSONString(newAfter)));
    }

    /**
     * test single navigation list.
     * @throws Exception
     */
    public void testSinglePageSnapshot3() throws Exception  {
        FakeReader reader = new FakeReader();
        reader.open();
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(new ReaderHelper(),
                reader, reader, reader, reader, new ReaderViewOptionsImpl());

        layoutManager.init();
        layoutManager.updateViewportSize();
        navigationArgs = new NavigationArgs();
        navigationArgs.rowsLeftToRight(NavigationArgs.Type.ALL, 3, 3, null);
        assertTrue(layoutManager.setCurrentLayout(PageConstants.SINGLE_PAGE_NAVIGATION_LIST, navigationArgs));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(PageConstants.SINGLE_PAGE_NAVIGATION_LIST));

        int rows = TestUtils.randInt(2, 10);
        int cols = TestUtils.randInt(2, 10);
        int count = rows * cols;
        NavigationArgs navigationArgs = new NavigationArgs();
        navigationArgs.rowsLeftToRight(NavigationArgs.Type.ALL, rows, cols, null);
        assertTrue(layoutManager.setNavigationArgs(navigationArgs));

        String position = reader.getInitPosition();
        layoutManager.gotoPosition(position);

        int index = 1;
        PageInfo pageInfo = layoutManager.getCurrentPageInfo();
        assertNotNull(pageInfo);
        while (layoutManager.nextScreen()) {
            pageInfo = layoutManager.getCurrentPageInfo();
            assertNotNull(pageInfo);
            if (!pageInfo.getName().equalsIgnoreCase(position)) {
                break;
            }
            ++index;
        }
        assertTrue(index == count);

        index = 1;
        pageInfo = layoutManager.getCurrentPageInfo();
        assertNotNull(pageInfo);
        while (layoutManager.prevScreen()) {
            pageInfo = layoutManager.getCurrentPageInfo();
            assertNotNull(pageInfo);
            if (!pageInfo.getName().equalsIgnoreCase(position)) {
                break;
            }
            ++index;
        }
        // go back to prev page cause additional counting.
        assertTrue(index == count + 1);

    }

}
