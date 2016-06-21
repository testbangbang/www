package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

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

    private ShapeModel randomShapeModel(final String documentId, final String pageUniqueId) {
        ShapeModel shapeModel = new ShapeModel();
        shapeModel.setDocumentUniqueId(documentId);
        shapeModel.setPageUniqueId(pageUniqueId);
        shapeModel.generateShapeUniqueId();
        return shapeModel;
    }

    public void testShapeDataProviderSave() {
        initDB();

        Map<String, ShapeModel> map = new HashMap<String, ShapeModel>();
        int max = TestUtils.randInt(10, 30);
        final String docId = UUID.randomUUID().toString();
        final String pageId = UUID.randomUUID().toString();
        for(int i = 0; i < max; ++i) {
            final ShapeModel model = randomShapeModel(docId, pageId);
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
}
