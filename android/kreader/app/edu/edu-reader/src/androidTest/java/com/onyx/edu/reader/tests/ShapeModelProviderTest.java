package com.onyx.edu.reader.tests;

import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhuzeng on 6/3/16.
 */
public class ShapeModelProviderTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ShapeModelProviderTest() {
        super(ReaderTestActivity.class);
    }

    private static ShapeModel randomScribble(final String documentUniqueId,
                                    final String pageName,
                                    final String subPageName,
                                    final RectF boundingRect,
                                    final TouchPointList points) {
        ShapeModel shapeModel = new ShapeModel();
        shapeModel.setDocumentUniqueId(documentUniqueId);
        shapeModel.setPageUniqueId(pageName);
        shapeModel.setShapeUniqueId(UUID.randomUUID().toString());
        shapeModel.setUpdatedAt(new Date());
        shapeModel.setCreatedAt(new Date());
        shapeModel.setBoundingRect(boundingRect);
        shapeModel.setSubPageName(subPageName);
        shapeModel.setExtraAttributes(UUID.randomUUID().toString());
        shapeModel.setPoints(points);
        shapeModel.setShapeType(0);
        return shapeModel;
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
        DataManager.cleanUp();
        List<ShapeModel> list = new ArrayList<ShapeModel>();
        final String md5 = UUID.randomUUID().toString();
        final String pageName = UUID.randomUUID().toString();
        final String subPageName = UUID.randomUUID().toString();
        final RectF boundingRect = new RectF(0, 0, 100, 100);
        final TouchPointList points = new TouchPointList();
        final TouchPoint point = randomPoint();
        points.getPoints().add(point);
        list.add(randomScribble(md5, pageName, subPageName, boundingRect, points));
        ShapeDataProvider.saveShapeList(getActivity(), list);
        List<ShapeModel> result = ShapeDataProvider.loadShapeList(getActivity(), md5, pageName, null);
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getBoundingRect().width() == boundingRect.width());
        assertTrue(result.get(0).getBoundingRect().height() == boundingRect.height());
        assertTrue(result.get(0).getPoints().size() == 1);

        TouchPoint resultPoint = result.get(0).getPoints().get(0);
        assertTrue(resultPoint.x == point.x);
        assertTrue(resultPoint.y == point.y);
        assertTrue(resultPoint.size == point.size);
    }

    public void testSaveAndRemove() {
        DataManager.cleanUp();
        List<ShapeModel> list = new ArrayList<ShapeModel>();
        final String md5 = UUID.randomUUID().toString();
        final String pageName = UUID.randomUUID().toString();
        final String subPageName = UUID.randomUUID().toString();
        final RectF boundingRect = new RectF(0, 0, 100, 100);
        final TouchPointList points = new TouchPointList();
        final TouchPoint point = randomPoint();
        points.getPoints().add(point);
        final ShapeModel shapeModel = randomScribble(md5, pageName, subPageName, boundingRect, points);
        list.add(shapeModel);
        ShapeDataProvider.saveShapeList(getActivity(), list);
        List<ShapeModel> result = ShapeDataProvider.loadShapeList(getActivity(), md5, pageName, null);
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getBoundingRect().width() == boundingRect.width());
        assertTrue(result.get(0).getBoundingRect().height() == boundingRect.height());
        assertTrue(result.get(0).getPoints().size() == 1);

        ShapeDataProvider.removeShape(getActivity(), shapeModel.getShapeUniqueId());
        result = ShapeDataProvider.loadShapeList(getActivity(), md5, pageName, null);
        assertTrue(result.size() <= 0);
    }
}
