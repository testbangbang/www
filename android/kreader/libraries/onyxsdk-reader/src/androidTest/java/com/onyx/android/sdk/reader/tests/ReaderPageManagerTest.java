package com.onyx.android.sdk.reader.tests;

import android.app.Application;
import android.graphics.PointF;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.android.sdk.reader.host.math.PageManager;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.navigation.NavigationList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 2/11/16.
 */
public class ReaderPageManagerTest extends ApplicationTestCase<Application> {

    public ReaderPageManagerTest() {
        super(Application.class);
    }

    public void testPageManager() throws Exception {
        PageManager pageManager = new PageManager();
        RectF viewport = new RectF(0, 0, TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));;
        pageManager.setViewportRect(viewport);
        PageInfo pageInfo = new PageInfo("1", TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));
        pageManager.add(pageInfo);
        pageManager.scaleToPage(pageInfo.getName());
        List<PageInfo> result = pageManager.getVisiblePages();

        for(PageInfo info : result) {
            float actualScale = info.getActualScale();
            float targetScale = PageUtils.scaleToPage(info.getOriginWidth(), info.getOriginHeight(), viewport.width(), viewport.height());
            assertTrue(TestUtils.compareFloatWhole(actualScale, targetScale));

            float left = (viewport.width() - targetScale * pageInfo.getOriginWidth()) / 2;
            float top = (viewport.height() - targetScale * pageInfo.getOriginHeight()) / 2;
            assertTrue(TestUtils.compareFloatWhole(left, info.getDisplayRect().left));
            assertTrue(TestUtils.compareFloatWhole(top, info.getDisplayRect().top));
        }
    }

    /**
     * Test scaleToViewport. the final scale should be matched and viewport position should be correct.
     * @throws Exception
     */
    public void testPageManager2() throws Exception {
        PageManager pageManager = new PageManager();
        RectF viewport = new RectF(TestUtils.randInt(10, 20),
                TestUtils.randInt(10, 20),
                TestUtils.randInt(1000, 2000),
                TestUtils.randInt(1000, 2000));

        int width = (int)viewport.width();
        int height = (int)viewport.height();
        RectF childInViewport = new RectF(
                TestUtils.randInt(20, width / 10),
                TestUtils.randInt(20, height / 10),
                TestUtils.randInt(width / 4, width / 2),
                TestUtils.randInt(height / 4, height / 2));
        RectF rectArg = new RectF(childInViewport);
        pageManager.setViewportRect(viewport);
        PageInfo pageInfo = new PageInfo("1", TestUtils.randInt(3000, 4000), TestUtils.randInt(3000, 4000));
        pageManager.add(pageInfo);

        pageManager.scaleToViewport(pageInfo.getName(), rectArg);

        PageInfo info = pageManager.getFirstVisiblePage();
        float actualScale = info.getActualScale();
        float targetScale = PageUtils.scaleToPage(childInViewport.width(), childInViewport.height(), viewport.width(), viewport.height());
        assertTrue(TestUtils.compareFloatWhole(actualScale, targetScale));

        float x = rectArg.centerX();
        float y = rectArg.centerY();
        float targetX = pageManager.getViewportRect().centerX();
        float targetY = pageManager.getViewportRect().centerY();
        assertTrue(TestUtils.compareFloatWhole(x, targetX) || TestUtils.compareFloatWhole(y, targetY));

        assertTrue(TestUtils.compareFloatWhole(rectArg.left, childInViewport.left * actualScale));
        assertTrue(TestUtils.compareFloatWhole(rectArg.top, childInViewport.top * actualScale));
        assertTrue(TestUtils.compareFloatWhole(rectArg.right, childInViewport.right * actualScale));
        assertTrue(TestUtils.compareFloatWhole(rectArg.bottom, childInViewport.bottom * actualScale));

        RectF resultViewport = pageManager.getViewportRect();
        // increase a little bit
        resultViewport.set(resultViewport.left - 1,
                resultViewport.top - 1,
                resultViewport.right + 1,
                resultViewport.bottom + 1);
        assertTrue(resultViewport.contains(rectArg));

    }


    public void testMath() {
        RectF a = new RectF(0, 0, 100, 100);
        RectF b = new RectF(50, 50, 60, 60);
        RectF.intersects(a, b);
        PageManager pageManager = new PageManager();
        for (int i = 0; i < 5000; ++i) {
            PageInfo pageInfo = new PageInfo(String.valueOf(i), TestUtils.randInt(100, 2000), TestUtils.randInt(100, 2000));
            pageManager.add(pageInfo);
        }
        pageManager.setViewportRect(new RectF(0, 0, 1024, 2000));
        long start = System.currentTimeMillis();
        pageManager.setScale(String.valueOf(0), 1.0f);
        long end = System.currentTimeMillis();
        Log.i("TEST", "update takes: " + (end - start));
        long end2 = System.currentTimeMillis();
        List<PageInfo> visiblePages = pageManager.collectVisiblePages();
        Log.i("TEST", "update takes: " + (end2 - end));

        List<PageInfo> verify = new ArrayList<PageInfo>();
        List<PageInfo> all = pageManager.getPageInfoList();
        for (PageInfo pageInfo : all) {
            if (RectF.intersects(pageManager.getViewportRect(), pageInfo.getPositionRect())) {
                verify.add(pageInfo);
            }
        }
        assertTrue(TestUtils.compareList(verify, visiblePages));
    }

    public void testMath2() {
        PageManager pageManager = new PageManager();
        pageManager.clear();
        PageInfo pageInfo = new PageInfo(String.valueOf(0), TestUtils.randInt(100, 2000), TestUtils.randInt(100, 2000));
        pageManager.add(pageInfo);
        pageManager.setScale(pageInfo.getName(), 1.0f);
        pageManager.setViewportRect(new RectF(0, 0, 2000, 2500));
        pageManager.scaleToPage(pageInfo.getName());
        assertTrue(TestUtils.compareFloatWhole(pageManager.getViewportRect().centerX(), pageManager.getPagesBoundingRect().centerX()));
        assertTrue(TestUtils.compareFloatWhole(pageManager.getViewportRect().centerY(), pageManager.getPagesBoundingRect().centerY()));

        assertFalse(pageManager.nextViewport());
        pageManager.nextViewport();
        assertFalse(pageManager.prevViewport());
        pageManager.prevViewport();
    }

    public void testMath3() {
        RectF child = new RectF(100, 100, 200, 200);
        RectF parent = new RectF(0, 0, 300, 300);
        float left = child.left;
        float top = child.top;
        float width = parent.width();
        float height = parent.height();

        float delta = PageUtils.scaleByRect(child, parent);
        assertTrue(delta > 0);
        assertTrue(TestUtils.compareFloatWhole((delta  * left), child.left));
        assertTrue(TestUtils.compareFloatWhole((delta  * top), child.top));


        float centerX = parent.centerX();
        float centerY = parent.centerY();
        float newCenterX = child.centerX();
        float newCenterY = child.centerY();
        assertTrue(TestUtils.compareFloatWhole(centerX, newCenterX));
        assertTrue(TestUtils.compareFloatWhole(centerY, newCenterY));
        assertTrue(TestUtils.compareFloatWhole(parent.width(), width));
        assertTrue(TestUtils.compareFloatWhole(parent.height(), height));
    }


    public void testMath4() {
        RectF child = new RectF(TestUtils.randInt(200, 500), TestUtils.randInt(200, 500), TestUtils.randInt(800, 1200), TestUtils.randInt(800, 1200));
        RectF parent = new RectF(TestUtils.randInt(0, 100), TestUtils.randInt(0, 100), TestUtils.randInt(1300, 2000), TestUtils.randInt(1300, 2000));
        float left = child.left;
        float top = child.top;
        float width = parent.width();
        float height = parent.height();

        float delta = PageUtils.scaleByRect(child, parent);
        assertTrue(delta > 0);
        assertTrue(Float.compare(delta  * left, child.left) == 0);
        assertTrue(Float.compare(delta  * top, child.top) == 0);

        float centerX = parent.centerX();
        float centerY = parent.centerY();
        float newCenterX = child.centerX();
        float newCenterY = child.centerY();
        assertTrue(TestUtils.compareFloatWhole(centerX, newCenterX));
        assertTrue(TestUtils.compareFloatWhole(centerY, newCenterY));
        assertTrue(TestUtils.compareFloatWhole(parent.width(), width));
        assertTrue(TestUtils.compareFloatWhole(parent.height(), height));
    }

    public void testMath5() {
        RectF entry = new RectF(0, 0, 500, 500);
        RectF parent = new RectF(0, 0, 1024, 768);
        int rows = 3, cols = 3;
        NavigationList navigator = NavigationList.rowsLeftToRight(3, 3, null);
        float actualScale;

        int index = 0;
        while (navigator.hasNext()) {
            RectF ratio = navigator.next();
            float left = 1.0f / cols * (index % cols);
            float top = 1.0f / rows * (index / cols);
            assertTrue(TestUtils.compareFloatWhole(ratio.left, left));
            assertTrue(TestUtils.compareFloatWhole(ratio.top, top));
            ++index;
            actualScale = PageUtils.scaleByRatio(ratio, entry.width(), entry.height(), parent);
        }
    }

    public void testMath6() {
        PointF point = new PointF(TestUtils.randInt(100, 500), TestUtils.randInt(100, 500));
        RectF parent = new RectF(TestUtils.randInt(100, 500), TestUtils.randInt(100, 500), 1024, 768);
        PointF test = new PointF(point.x, point.y);
        PageUtils.translateCoordinates(test, parent);
        assertTrue(TestUtils.compareFloatWhole(test.x, point.x - parent.left));
        assertTrue(TestUtils.compareFloatWhole(test.y, point.y - parent.top));
    }
}
