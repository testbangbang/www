package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.scribble.data.ShapeLibraryDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeLibraryModel;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

import java.util.List;
import java.util.UUID;

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
        List<ShapeLibraryModel> list = ShapeLibraryDataProvider.loadShapeDocumentList(getContext(), document.getDocumentUniqueId());
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getDocumentUniqueId().equalsIgnoreCase(document.getDocumentUniqueId()));
        ShapeLibraryDataProvider.removeShapeDocument(getContext(), document.getDocumentUniqueId());
        list = ShapeLibraryDataProvider.loadShapeDocumentList(getContext(), document.getDocumentUniqueId());
        assertTrue(list.size() == 0);
    }

    public void testMove() {
        initDB();
        final ShapeLibraryModel document = randomShapeModel();
        ShapeLibraryDataProvider.saveShapeDocument(getContext(), document);
        List<ShapeLibraryModel> list = ShapeLibraryDataProvider.loadShapeDocumentList(getContext(), document.getDocumentUniqueId());
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getDocumentUniqueId().equalsIgnoreCase(document.getDocumentUniqueId()));

        final String newParentId = UUID.randomUUID().toString();
        ShapeLibraryDataProvider.moveShapeDocument(getContext(), document.getDocumentUniqueId(), newParentId);

        list = ShapeLibraryDataProvider.loadShapeDocumentList(getContext(), document.getDocumentUniqueId());
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getParentUniqueId().equalsIgnoreCase(document.getParentUniqueId()));

    }


}
