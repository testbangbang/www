package com.onyx.edu.reader.tests;

import android.graphics.Color;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.onyx.android.sdk.data.request.data.db.CreateDBRequest;
import com.onyx.android.sdk.data.request.data.db.ExportDBRequest;
import com.onyx.android.sdk.data.request.data.db.ExportDataToDBRequest;
import com.onyx.android.sdk.data.request.data.db.ImportDBRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.model.ReaderNoteDatabase;
import com.onyx.edu.reader.note.model.ReaderNoteShapeModel;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/5/22.
 */

public class ImportExportDBTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private static final String TAG = "ImportExportDBTest";

    public ImportExportDBTest() {
        super(ReaderTestActivity.class);
    }

    private TouchPoint randomTouchPoint() {
        return new TouchPoint(
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000));
    }

    private RectF randomRectF() {
        return new RectF(TestUtils.randInt(0, 1000), TestUtils.randInt(0, 1000), TestUtils.randInt(0, 1000), TestUtils.randInt(0, 1000));
    }

    private ReaderNoteShapeModel randomShape(final String documentUniqueId, int minPoint, int maxPoint) {
        ReaderNoteShapeModel shape = new ReaderNoteShapeModel();
        shape.setShapeUniqueId(ShapeUtils.generateUniqueId());
        shape.setDocumentUniqueId(documentUniqueId);
        shape.setPageUniqueId(ShapeUtils.generateUniqueId());
        shape.setSubPageUniqueId(ShapeUtils.generateUniqueId());
        shape.setColor(Color.BLACK);
        int touchPointSize = TestUtils.randInt(minPoint, maxPoint);;
        TouchPointList pt = new TouchPointList();
        for (int j = 0; j < touchPointSize; j++) {
            pt.add(randomTouchPoint());
        }
        shape.setBoundingRect(randomRectF());
        shape.setPoints(pt);
        shape.setShapeType(2);
        return shape;
    }

    public void testNormalExportImportDb() {
        String documentUniqueId = ShapeUtils.generateUniqueId();
        int count = 10000;
        removeAllShapeOfDocument(documentUniqueId);
        addShapesToDatabase(count, documentUniqueId);
        exportDB(count, documentUniqueId);
        removeAllShapeOfDocument(documentUniqueId);
        importDB(count, documentUniqueId);
        ReaderNoteDataProvider.clear(getActivity());
    }

    public void testExportDataToDb() {
        String documentUniqueId = ShapeUtils.generateUniqueId();
        int count = 10000;
        ReaderNoteDataProvider.clear(getActivity());
        addShapesToDatabase(count, documentUniqueId);
        exportDataToDB(count, documentUniqueId);
        ReaderNoteDataProvider.clear(getActivity());
    }

    private String createExportDB() {
        String exportDBPath = "mnt/sdcard/test.db";
        FileUtils.deleteFile(exportDBPath);
        String currentDbPath = getActivity().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        CreateDBRequest createDBRequest = new CreateDBRequest(currentDbPath, exportDBPath);
        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        try {
            long startTime = System.currentTimeMillis();
            createDBRequest.execute(readerDataHolder.getDataManager());
            Log.d(TAG, "create export db use " + (System.currentTimeMillis() - startTime) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        return exportDBPath;
    }

    private void exportDataToDB(int count, String documentUniqueId) {
        String currentDbPath = getActivity().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String condition = "documentUniqueId='"+documentUniqueId+"' ";
        String table = "ReaderNoteShapeModel";
        String exportDBPath = createExportDB();
        ExportDataToDBRequest exportDataToDBRequest =  new ExportDataToDBRequest(currentDbPath, exportDBPath, condition, table);
        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        try {
            long startTime = System.currentTimeMillis();
            exportDataToDBRequest.execute(readerDataHolder.getDataManager());
            Log.d(TAG, "export shape data to new db: data count: "+ count+ " \r\nuse " + (System.currentTimeMillis() - startTime) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        long loadStartTime = System.currentTimeMillis();
        List<ReaderNoteShapeModel> shapeModels = ReaderNoteDataProvider.loadShapeList(getActivity(), documentUniqueId);
        Log.d(TAG, "load db from database: data count: "+ count+ " \r\nuse " + (System.currentTimeMillis() - loadStartTime) + "毫秒");
        assertTrue(count == shapeModels.size());
    }

    private void exportDB(int count, String documentUniqueId) {
        String currentDbPath = getActivity().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String exportFilePath = "mnt/sdcard/shape.csv";
        String condition = "select * from ReaderNoteShapeModel where documentUniqueId='"+documentUniqueId+"' ";
        ExportDBRequest exportDBRequest = new ExportDBRequest(currentDbPath, exportFilePath, condition);
        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        try {
            long startTime = System.currentTimeMillis();
            exportDBRequest.execute(readerDataHolder.getDataManager());
            Log.d(TAG, "export db to csv file: data count: "+ count+ " \r\nuse " + (System.currentTimeMillis() - startTime) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private void importDB(int count, String documentUniqueId) {
        String currentDbPath = getActivity().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String importFilePath = "mnt/sdcard/shape.csv";
        String table = "ReaderNoteShapeModel";
        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        ImportDBRequest importDBRequest = new ImportDBRequest(currentDbPath, importFilePath, table);

        try {
            long startTime = System.currentTimeMillis();
            importDBRequest.execute(readerDataHolder.getDataManager());
            Log.d(TAG, "import db to csv file: data count: "+ count+ " \r\nuse " + (System.currentTimeMillis() - startTime) + "毫秒");
            long loadStartTime = System.currentTimeMillis();
            List<ReaderNoteShapeModel> shapeModels = ReaderNoteDataProvider.loadShapeList(getActivity(), documentUniqueId);
            Log.d(TAG, "load db from database: data count: "+ count+ " \r\nuse " + (System.currentTimeMillis() - loadStartTime) + "毫秒");
            assertTrue(count == shapeModels.size());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private void removeAllShapeOfDocument(final String documentUniqueId) {
        ReaderNoteDataProvider.removeAllShapeOfDocument(getActivity(), documentUniqueId);
        List<ReaderNoteShapeModel> shapeList = ReaderNoteDataProvider.loadShapeList(getActivity(), documentUniqueId);
        assertTrue(shapeList.size() == 0);
    }

    private void addShapesToDatabase(int count, String documentUniqueId) {
        List<ReaderNoteShapeModel> shapeModels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            shapeModels.add(randomShape(documentUniqueId, 50, 100));
        }
        long saveStartTime = System.currentTimeMillis();
        ReaderNoteDataProvider.saveShapeList(getActivity(), shapeModels);
        Log.d(TAG, "save shape to db: data count: "+ count+ " \r\nuse " + (System.currentTimeMillis() - saveStartTime) + "毫秒");
    }
}
