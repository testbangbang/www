package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
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

    private List<PageInfo> randPageList() {
        PageManager pageManager = new PageManager();
        RectF viewport = new RectF(0, 0, TestUtils.randInt(10000, 20000), TestUtils.randInt(10000, 20000));
        pageManager.setViewportRect(viewport.left, viewport.top, viewport.width(), viewport.height());
        int count = TestUtils.randInt(1, 10);
        for(int i = 0; i < count; ++i) {
            PageInfo pageInfo = new PageInfo(String.valueOf(i), TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));
            pageManager.add(pageInfo);
        }
        int index = TestUtils.randInt(1, count);
        pageManager.scaleToPage(String.valueOf(index));
        return pageManager.getVisiblePages();
    }

    public void testCacheKey() throws Exception {
        ReaderCacheManager cacheManager = new ReaderCacheManager();
        List<PageInfo> list = randPageList();
        final String key = PositionSnapshot.cacheKey(list);
        ReaderBitmapImpl bitmap = ReaderBitmapImpl.create(100, 100, Bitmap.Config.ARGB_8888);
        assertNotNull(cacheManager.addBitmap(key, bitmap));
        assertNotNull(cacheManager.getBitmap(key));
    }

    public void testPositionKey() throws Exception {
    }

}
