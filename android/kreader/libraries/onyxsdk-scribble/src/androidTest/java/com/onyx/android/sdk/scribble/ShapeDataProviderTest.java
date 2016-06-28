package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.*;

/**
 * Created by zhuzeng on 6/21/16.
 */
public class ShapeDataProviderTest extends ApplicationTestCase<Application> {

    private static boolean init = false;

    public ShapeDataProviderTest() {
        super(Application.class);
    }

    private void initDB() {
        if (init) {
            return;
        }
        FlowConfig.Builder builder = new FlowConfig.Builder(getContext());
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
        init = true;
    }

    private TouchPoint randomTouchPoint() {
        TouchPoint touchPoint = new TouchPoint(
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000));
        return touchPoint;
    }

    private ShapeModel randomShapeModel(final String documentId, final String pageUniqueId, int min, int max) {
        ShapeModel shapeModel = new ShapeModel();
        shapeModel.setDocumentUniqueId(documentId);
        shapeModel.setPageUniqueId(pageUniqueId);
        shapeModel.generateShapeUniqueId();
        int points = TestUtils.randInt(min, max);
        final TouchPointList list = new TouchPointList(points);
        for(int i = 0; i < points; ++i) {
            list.add(randomTouchPoint());
        }
        shapeModel.setPoints(list);
        return shapeModel;
    }

    public void testShapeDataProviderSave() {
        initDB();

        Map<String, ShapeModel> map = new HashMap<String, ShapeModel>();
        int max = TestUtils.randInt(10, 30);
        final String docId = UUID.randomUUID().toString();
        final String pageId = UUID.randomUUID().toString();
        for(int i = 0; i < max; ++i) {
            final ShapeModel model = randomShapeModel(docId, pageId, 10, 1000);
            map.put(model.getShapeUniqueId(), model);
        }

        ShapeDataProvider.saveShapeList(getContext(), map.values());
        List<ShapeModel> result = ShapeDataProvider.loadShapeList(getContext(), docId, pageId, null);
        assertNotNull(result);
        assertTrue(result.size() == map.size());

        for(ShapeModel mode: result) {
            assertTrue(map.containsKey(mode.getShapeUniqueId()));
        }
    }

    public void testShapeDataProviderSave2() {
        initDB();

        Map<String, ShapeModel> map = new HashMap<String, ShapeModel>();
        int max = TestUtils.randInt(10, 30);
        final String docId = UUID.randomUUID().toString();
        final String pageId = UUID.randomUUID().toString();
        for(int i = 0; i < max; ++i) {
            final ShapeModel model = randomShapeModel(docId, pageId, 10, 1000);
            map.put(model.getShapeUniqueId(), model);
        }

        ShapeDataProvider.saveShapeList(getContext(), map.values());
        List<ShapeModel> result = ShapeDataProvider.loadShapeList(getContext(), docId, pageId, null);
        assertNotNull(result);
        assertTrue(result.size() == map.size());

        for(ShapeModel mode: result) {
            final ShapeModel origin = map.get(mode.getShapeUniqueId());
            assertTrue(origin.getPoints().size() == mode.getPoints().size());
            int size = origin.getPoints().size();
            for(int i = 0; i < size; ++i) {
                TouchPoint src = origin.getPoints().get(i);
                TouchPoint dst = mode.getPoints().get(i);
                assertEquals(src.getX(), dst.getX());
                assertEquals(src.getY(), dst.getY());
                assertEquals(src.getSize(), dst.getSize());
                assertEquals(src.getPressure(), dst.getPressure());
                assertEquals(src.getTimestamp(), dst.getTimestamp());
            }
        }
    }

    // for normal writing, less than 300 points per stroke
    // less than 300 shapes per page.
    public void testShapeDataProviderSaveBenchmark() {
        initDB();
        Delete.tables(ShapeModel.class);

        // generate lot of shapes and each shape contains lot of points.
        int max = TestUtils.randInt(10, 300);
        final String docId = ShapeUtils.generateUniqueId();
        final String pageId = ShapeUtils.generateUniqueId();
        List<ShapeModel> list = new ArrayList<ShapeModel>();
        int count = 0;
        for(int i = 0; i < max; ++i) {
            ShapeModel model = randomShapeModel(docId, pageId, 50, 300);
            list.add(model);
            count += model.getPoints().size();
        }

        long start = System.currentTimeMillis();
        ShapeDataProvider.saveShapeList(getContext(), list);
        long end = System.currentTimeMillis();
        Log.e("Benchmark", "save shapes " + max + " points: " + count + " takes: " + (end - start));
    }
}
