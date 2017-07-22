package com.onyx.android.note.tests;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.note.test.NoteTestActivity;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeDatabase;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.request.note.TransferDBRequest;
import com.onyx.android.sdk.utils.DatabaseUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

import java.util.List;
import java.util.UUID;

/**
 * Created by ming on 2017/4/24.
 */

public class BackupRestoreNoteDBTest extends ActivityInstrumentationTestCase2<NoteTestActivity> {

    private static final String TAG = "BackupRestoreNoteDBTest";

    public BackupRestoreNoteDBTest() {
        super(NoteTestActivity.class);
    }

    private static final String docId = "test_doc_id";
    private static final String shapeUniqueId = "test_shape_unique_id";


    public void testRestoreNoteDB() {
        initDatabase();
        final String docId = UUID.randomUUID().toString();
        final String shapeUniqueId = UUID.randomUUID().toString();
        restoreDB(docId, shapeUniqueId);
    }

    public void testRestoreLowVersionNoteDB() {
        initDatabase();
        TestUtils.sleep(3000);
        downgradeBackupDB();
        restoreDB(docId, shapeUniqueId);
    }

    public void testBackupNoteDB() {
        initDatabase();
        backupDB(docId, shapeUniqueId);
    }

    public void backupDB(final String docId, final String shapeUniqueId) {
        ShapeDataProvider.clear();
        NoteDataProvider.clear();

        NoteModel noteModel = NoteModel.createNote(docId, "", "test");
        noteModel.save();
        ShapeModel shapeModel = new ShapeModel();
        shapeModel.setShapeUniqueId(shapeUniqueId);
        shapeModel.setDocumentUniqueId(docId);
        shapeModel.save();

        final String backupDBPath = getbackupDBPath();
        String currentDBPath = getCurrentDBPath();

        TransferDBRequest restoreDBRequest = new TransferDBRequest(currentDBPath, backupDBPath, false, false, null);
        try {
            restoreDBRequest.execute(null);
            SQLiteDatabase database = SQLiteDatabase.openDatabase(backupDBPath, null,SQLiteDatabase.OPEN_READWRITE);
            int version = database.getVersion();

            Cursor shapeCursor = database.query(ShapeModel.class.getSimpleName(), new String[] {"shapeUniqueId"}, null, null, null, null, null);
            assertTrue(shapeCursor.getCount() > 0);
            if (shapeCursor.moveToFirst()) {
                String value = shapeCursor.getString(0);
                assertTrue(value.equals(shapeUniqueId));
            }
            shapeCursor.close();

            Cursor noteCursor = database.query(NoteModel.class.getSimpleName(), new String[] {"uniqueId"}, null, null, null, null, null);
            assertTrue(noteCursor.getCount() > 0);
            if (noteCursor.moveToFirst()) {
                String value = noteCursor.getString(0);
                assertTrue(value.equals(docId));
            }
            noteCursor.close();


            database.close();
            assertTrue(version == DatabaseUtils.getDBVersion(currentDBPath));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void restoreDB(final String docId, final String shapeUniqueId) {
        ShapeDataProvider.clear();
        NoteDataProvider.clear();
        String currentDBPath = getCurrentDBPath();
        List<ShapeModel> shapeModels = ShapeDataProvider.loadShapeList(getActivity());
        assertTrue(shapeModels.size() == 0);
        List<NoteModel> noteModels = NoteDataProvider.loadAllNoteList();
        assertTrue(noteModels.size() == 0);

        String backupFilePath = getbackupDBPath();

        TransferDBRequest restoreDBRequest = new TransferDBRequest(backupFilePath, currentDBPath, true, true, ShapeGeneratedDatabaseHolder.class);
        try {
            restoreDBRequest.execute(null);
            TestUtils.sleep(2000);
            shapeModels = ShapeDataProvider.loadShapeList(getActivity());
            assertTrue(shapeModels.size() == 1);
            assertTrue(shapeModels.get(0).getShapeUniqueId().equals(shapeUniqueId));

            noteModels.clear();
            noteModels = NoteDataProvider.loadAllNoteList();
            assertTrue(noteModels.size() == 1);
            assertTrue(noteModels.get(0).getUniqueId().equals(docId));

            // test has groupId field
            SQLiteDatabase database = SQLiteDatabase.openDatabase(currentDBPath, null,SQLiteDatabase.OPEN_READWRITE);
            database.execSQL("INSERT INTO ShapeModel (groupId) VALUES ('test');");
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downgradeBackupDB() {
        String backupFilePath = "mnt/sdcard/" + ShapeDatabase.NAME + ".db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(backupFilePath, null,SQLiteDatabase.OPEN_READWRITE);
        // test can auto add groupId field by restore
        database.setVersion(1);
        database.execSQL("create table temp as select shapeUniqueId, documentUniqueId from ShapeModel;");
        database.execSQL("drop table ShapeModel;");
        database.execSQL("alter table temp rename to ShapeModel; ");
        database.close();
        TestUtils.sleep(2000);
    }

    private void initDatabase() {
        Log.d(TAG, "currentDBPath: " + getCurrentDBPath());
        FileUtils.deleteFile(getCurrentDBPath());
        FlowConfig.Builder builder = new FlowConfig.Builder(getActivity());
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
    }

    private String getCurrentDBPath() {
        return getActivity().getDatabasePath(ShapeDatabase.NAME).getPath()+ ".db";
    }

    private String getbackupDBPath() {
        return "mnt/sdcard/" + ShapeDatabase.NAME + ".db";
    }

}
