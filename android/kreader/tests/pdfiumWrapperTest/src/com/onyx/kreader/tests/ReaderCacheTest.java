package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.wrapper.ReaderCacheManager;
import com.onyx.kreader.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 2/17/16.
 */
public class ReaderCacheTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderCacheTest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }
    private PageManager pageManager = new PageManager();

    private List<PageInfo> randPageList() {
        RectF viewport = new RectF(0, 0, TestUtils.randInt(1000, 2000), TestUtils.randInt(10000, 20000));
        pageManager.setViewportRect(viewport.left, viewport.top, viewport.width(), viewport.height());
        int count = TestUtils.randInt(1, 100);
        pageManager.clear();
        for(int i = 0; i < count; ++i) {
            PageInfo pageInfo = new PageInfo(String.valueOf(i), TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));
            pageManager.add(pageInfo);
        }
        return pageManager.getPageInfoList();
    }

    public void testCacheKey() throws Exception {
        ReaderCacheManager cacheManager = new ReaderCacheManager();
        List<PageInfo> list = randPageList();
        final String key = PositionSnapshot.cacheKey(list);
        ReaderBitmapImpl bitmap = ReaderBitmapImpl.create(100, 100, Bitmap.Config.ARGB_8888);
        assertNotNull(cacheManager.addBitmap(key, bitmap));
        assertNotNull(cacheManager.getBitmap(key));
    }

    public void testViewport() {
        for(int i = 0; i < 1000; ++i) {
            List<PageInfo> list = randPageList();
            int index = TestUtils.randInt(0, list.size() - 1);
            final PageInfo pageInfo = list.get(index);
            pageManager.setViewportPosition(pageInfo.getName(), 0, 0);
            List<PageInfo> visibleList = pageManager.getVisiblePages();
            assertTrue(visibleList.size() > 0);
            final PageInfo first = visibleList.get(0);

            // in screen coordinates system, it should be on top
            if (pageManager.getViewportRect().height() <= pageManager.getPagesBoundingRect().height() &&
                pageManager.getPagesBoundingRect().height() - first.getPositionRect().top <= pageManager.getViewportRect().height()) {
                assertTrue(TestUtils.compareFloatWhole(first.getDisplayRect().top, 0));
                assertTrue(TestUtils.compareFloatWhole(first.getPositionRect().top, pageManager.getViewportRect().top));
                assertTrue(first.getName().equals(pageInfo.getName()));
            }

            for (PageInfo visiblePageInfo : visibleList) {
                final RectF viewport = pageManager.getViewportRect();
                final RectF position = visiblePageInfo.getPositionRect();
                final RectF display = visiblePageInfo.getDisplayRect();
                if (!RectF.intersects(viewport, position)) {
                    assertTrue(false);
                }
                assertTrue(display.top <= viewport.height());
            }
        }
    }

}
