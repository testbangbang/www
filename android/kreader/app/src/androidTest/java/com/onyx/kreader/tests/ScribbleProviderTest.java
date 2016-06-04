package com.onyx.kreader.tests;

import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import android.text.method.Touch;
import com.onyx.kreader.dataprovider.DataProvider;
import com.onyx.kreader.scribble.data.Scribble;
import com.onyx.kreader.scribble.data.ScribbleDataProvider;
import com.onyx.kreader.scribble.data.TouchPoint;
import com.onyx.kreader.scribble.data.TouchPointList;
import com.onyx.kreader.utils.TestUtils;
import com.raizlabs.android.dbflow.data.Blob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhuzeng on 6/3/16.
 */
public class ScribbleProviderTest   extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ScribbleProviderTest() {
        super(ReaderTestActivity.class);
    }

    private static Scribble randomScribble(final String md5,
                                    final String pageName,
                                    final String subPageName,
                                    final RectF boundingRect,
                                    final TouchPointList points) {
        Scribble scribble = new Scribble();
        scribble.setMd5(md5);
        scribble.setPageName(pageName);
        scribble.setUniqueId(UUID.randomUUID().toString());
        scribble.setUpdatedAt(new Date());
        scribble.setCreatedAt(new Date());
        scribble.setBoundingRect(boundingRect);
        scribble.setSubPageName(subPageName);
        scribble.setExtraAttributes(UUID.randomUUID().toString());
        scribble.setPoints(points);
        scribble.setPosition(UUID.randomUUID().toString());
        scribble.setScribbleType(0);
        return scribble;
    }

    private static TouchPoint randomPoint() {
        TouchPoint touchPoint = new TouchPoint();
        touchPoint.x = TestUtils.randInt(0, 100);
        touchPoint.y = TestUtils.randInt(0, 100);
        touchPoint.pressure = TestUtils.randInt(0, 100);
        touchPoint.size = TestUtils.randInt(0, 100);
        touchPoint.timestamp = TestUtils.randInt(0, 100);
        return touchPoint;
    }

    public void testSave() {
        DataProvider.cleanUp();
        List<Scribble> list = new ArrayList<Scribble>();
        final String md5 = UUID.randomUUID().toString();
        final String pageName = UUID.randomUUID().toString();
        final String subPageName = UUID.randomUUID().toString();
        final RectF boundingRect = new RectF(0, 0, 100, 100);
        final TouchPointList points = new TouchPointList();
        final TouchPoint point = randomPoint();
        points.getPoints().add(point);
        list.add(randomScribble(md5, pageName, subPageName, boundingRect, points));
        ScribbleDataProvider.saveScribbleList(getActivity(), list);
        List<Scribble> result = ScribbleDataProvider.loadScribbleList(getActivity(), md5, pageName, null);
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getBoundingRect().width() == boundingRect.width());
        assertTrue(result.get(0).getBoundingRect().height() == boundingRect.height());
        assertTrue(result.get(0).getPoints().size() == 1);

        TouchPoint resultPoint = result.get(0).getPoints().get(0);
        assertTrue(resultPoint.x == point.x);
        assertTrue(resultPoint.y == point.y);
        assertTrue(resultPoint.size == point.size);
    }
}
