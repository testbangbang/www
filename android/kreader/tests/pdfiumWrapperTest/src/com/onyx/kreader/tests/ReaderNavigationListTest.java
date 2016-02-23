package com.onyx.kreader.tests;

import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.utils.TestUtils;

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
        NavigationArgs navigationArgs = NavigationArgs.rowsLeftToRight(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().subScreenCount() == total);

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
        NavigationArgs navigationArgs = NavigationArgs.rowsRightToLeft(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().subScreenCount() == total);

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
        NavigationArgs navigationArgs = NavigationArgs.columnsLeftToRight(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().subScreenCount() == total);

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
        NavigationArgs navigationArgs = NavigationArgs.columnsRightToLeft(NavigationArgs.Type.ALL, rows, cols, null);
        RectF rect;
        assertTrue(navigationArgs.getList().subScreenCount() == total);

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

}
