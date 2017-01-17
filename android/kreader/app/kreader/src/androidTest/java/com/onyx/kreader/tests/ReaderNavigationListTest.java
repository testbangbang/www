package com.onyx.kreader.tests;

import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;


/**
 * Created by zengzhu on 2/23/16.
 */
public class ReaderNavigationListTest  extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderNavigationListTest() {
        super(ReaderTestActivity.class);
    }

    public void testNavigationList1() {
        int rows = TestUtils.randInt(2, 10);
        int cols = TestUtils.randInt(2, 20);
        int total = rows * cols;
        NavigationArgs navigationArgs = new NavigationArgs();
        navigationArgs.rowsLeftToRight(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().getSubScreenCount() == total);

        int index = 0;

        while (navigationArgs.getList().hasNext()) {
            rect = navigationArgs.getList().next();
            float left = (float)(index % cols) * 1.0f / cols;
            float top = (float)(index / cols) * 1.0f / rows;
            assertTrue(TestUtils.compareFloatWhole(rect.left, left));
            assertTrue(TestUtils.compareFloatWhole(rect.top, top));
            ++index;
        }
    }

    public void testNavigationList2() {
        int rows = TestUtils.randInt(2, 10);
        int cols = TestUtils.randInt(2, 20);
        int total = rows * cols;
        NavigationArgs navigationArgs = new NavigationArgs();
        navigationArgs.rowsRightToLeft(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().getSubScreenCount() == total);

        int index = 0;
        while (navigationArgs.getList().hasNext()) {
            rect = navigationArgs.getList().next();
            float left = (float)(cols - 1 - index % cols) * 1.0f / cols;
            float top = (float)(index / cols) * 1.0f / rows;
            assertTrue(TestUtils.compareFloatWhole(rect.left, left));
            assertTrue(TestUtils.compareFloatWhole(rect.top, top));
            ++index;
        }
    }

    public void testNavigationList3() {
        int rows = TestUtils.randInt(2, 10);
        int cols = TestUtils.randInt(2, 20);
        int total = rows * cols;
        NavigationArgs navigationArgs = new NavigationArgs();
        navigationArgs.columnsLeftToRight(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().getSubScreenCount() == total);

        int index = 0;
        while (navigationArgs.getList().hasNext()) {
            rect = navigationArgs.getList().next();
            float left = (float)(index / rows) * 1.0f / cols;
            float top = (float)(index % rows) * 1.0f / rows;
            assertTrue(TestUtils.compareFloatWhole(rect.left, left));
            assertTrue(TestUtils.compareFloatWhole(rect.top, top));
            ++index;
        }
    }

    public void testNavigationList4() {
        int rows = TestUtils.randInt(2, 10);
        int cols = TestUtils.randInt(2, 10);
        int total = rows * cols;
        NavigationArgs navigationArgs = new NavigationArgs();
        navigationArgs.columnsRightToLeft(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().getSubScreenCount() == total);

        int index = 0;
        while (navigationArgs.getList().hasNext()) {
            rect = navigationArgs.getList().next();
            float left = (float)(cols - 1 - index / rows) * 1.0f / cols;
            float top = (float)(index % rows) * 1.0f / rows;
            assertTrue(TestUtils.compareFloatWhole(rect.left, left));
            assertTrue(TestUtils.compareFloatWhole(rect.top, top));
            ++index;
        }
    }

    public void testNavigationList5() {
        int rows = TestUtils.randInt(2, 10);
        int cols = TestUtils.randInt(2, 20);
        int total = rows * cols;
        NavigationArgs navigationArgs = new NavigationArgs();
        navigationArgs.rowsLeftToRight(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().getSubScreenCount() == total);

        RectF viewport = new RectF(0, 0, TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));
        int index = 0;
        while (navigationArgs.getList().hasNext()) {
            rect = navigationArgs.getList().next();
            float left = (float)(index % cols) * 1.0f / cols;
            float top = (float)(index / cols) * 1.0f / rows;
            assertTrue(TestUtils.compareFloatWhole(rect.left, left));
            assertTrue(TestUtils.compareFloatWhole(rect.top, top));

            RectF displayRect = NavigationArgs.rectInViewport(rect, navigationArgs.getList().getLimitedRect(), viewport);
            assertNotNull(displayRect);
            float scale = PageUtils.scaleToPage(displayRect.width(), displayRect.height(), viewport.width(), viewport.height());
            assertTrue(scale >= 1.0f);
            ++index;
        }
    }

}
