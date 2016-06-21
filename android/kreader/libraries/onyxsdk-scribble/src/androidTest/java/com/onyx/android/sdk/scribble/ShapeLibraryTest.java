package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.scribble.data.ShapeLibraryDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeLibraryModel;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.*;

/**
 * Created by zhuzeng on 6/21/16.
 */
public class ShapeLibraryTest extends ApplicationTestCase<Application> {

    private static boolean init = false;

    public ShapeLibraryTest() {
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

    private ShapeLibraryModel randomShapeModel() {
        ShapeLibraryModel shapeModel = new ShapeLibraryModel();
        shapeModel.setDocumentUniqueId(UUID.randomUUID().toString());
        return shapeModel;
    }

    public void testSave() {
        initDB();
        final ShapeLibraryModel document = randomShapeModel();
        ShapeLibraryDataProvider.saveShapeDocument(getContext(), document);
        ShapeLibraryModel model = ShapeLibraryDataProvider.loadShapeDocument(getContext(), document.getDocumentUniqueId());
        assertNotNull(model);
        assertTrue(model.getDocumentUniqueId().equalsIgnoreCase(document.getDocumentUniqueId()));
        ShapeLibraryDataProvider.removeShapeDocument(getContext(), document.getDocumentUniqueId());
        model = ShapeLibraryDataProvider.loadShapeDocument(getContext(), document.getDocumentUniqueId());
        assertNull(model);
    }

    public void testMove() {
        initDB();
        final ShapeLibraryModel document = randomShapeModel();
        ShapeLibraryDataProvider.saveShapeDocument(getContext(), document);
        ShapeLibraryModel model = ShapeLibraryDataProvider.loadShapeDocument(getContext(), document.getDocumentUniqueId());
        assertNotNull(model);
        assertTrue(model.getDocumentUniqueId().equalsIgnoreCase(document.getDocumentUniqueId()));

        final String newParentId = UUID.randomUUID().toString();
        ShapeLibraryDataProvider.moveShapeDocument(getContext(), document.getDocumentUniqueId(), newParentId);

        model = ShapeLibraryDataProvider.loadShapeDocument(getContext(), document.getDocumentUniqueId());
        assertNotNull(model);
        assertTrue(model.getParentUniqueId().equalsIgnoreCase(newParentId));
    }

    public void testDocumentList() {
        initDB();
        Delete.tables(ShapeLibraryModel.class);
        int max = TestUtils.randInt(10, 30);
        Map<String, ShapeLibraryModel> map = new HashMap<String, ShapeLibraryModel>();
        for(int i = 0; i < max; ++i) {
            final ShapeLibraryModel document = randomShapeModel();
            ShapeLibraryDataProvider.saveShapeDocument(getContext(), document);
            map.put(document.getDocumentUniqueId(), document);
        }
        List<ShapeLibraryModel> list = ShapeLibraryDataProvider.loadShapeDocumentList(getContext(), null);
        assertTrue(list.size() == map.size());
        for(ShapeLibraryModel model : list) {
            assertTrue(map.containsKey(model.getDocumentUniqueId()));
        }

        int limit = TestUtils.randInt(1, max);
        List<String> newLibrary = new ArrayList<String>();
        for(int i = 0; i < limit; ++i) {
            final String newLibraryId = UUID.randomUUID().toString();
            newLibrary.add(newLibraryId);
            final ShapeLibraryModel model = list.get(i);
            ShapeLibraryDataProvider.moveShapeDocument(getContext(), model.getDocumentUniqueId(), newLibraryId);
        }

        for(String string: newLibrary) {
            final List<ShapeLibraryModel> subList = ShapeLibraryDataProvider.loadShapeDocumentList(getContext(), string);
            assertTrue(subList.size() == 1);
            final ShapeLibraryModel model = subList.get(0);
            assertTrue(model.getParentUniqueId().equalsIgnoreCase(string));
        }
    }

}
