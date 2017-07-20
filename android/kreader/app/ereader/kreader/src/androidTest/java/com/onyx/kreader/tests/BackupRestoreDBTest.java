package com.onyx.kreader.tests;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.ActivityInstrumentationTestCase2;
import android.text.TextUtils;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.request.data.db.BackupRestoreDBRequest;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.kreader.ui.actions.BackupRestoreDBAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ReaderNoteGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/4/24.
 */

public class BackupRestoreDBTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private static final String TAG = "BackupRestoreDBTest";

    public BackupRestoreDBTest() {
        super(ReaderTestActivity.class);
    }

    private static final String test = "ashjbfhjasbfhjsabfjhbsajfbsafa";

    public void testBackupDB() {
        String dbPath = getActivity().getDatabasePath(OnyxStatisticsDatabase.NAME).getPath()+ ".db";
        FileUtils.deleteFile(dbPath);
        initDatabase();
        StatisticsUtils.deleteStatisticsList(getActivity());
        OnyxStatisticsModel statisticsModel = new OnyxStatisticsModel();
        statisticsModel.setNote(test);
        statisticsModel.save();
        Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap = new HashMap<>();
        final String backupDBPath = "mnt/sdcard/" + OnyxStatisticsDatabase.NAME + ".db";
        backupRestoreDBMap.put(DatabaseInfo.create(OnyxStatisticsDatabase.NAME, OnyxStatisticsDatabase.VERSION, dbPath),
                DatabaseInfo.create(backupDBPath));

        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        BackupRestoreDBRequest restoreDBRequest = new BackupRestoreDBRequest(backupRestoreDBMap, true);
        try {
            restoreDBRequest.execute(readerDataHolder.getDataManager());
            SQLiteDatabase database = SQLiteDatabase.openDatabase(backupDBPath, null,SQLiteDatabase.OPEN_READWRITE);
            int version = database.getVersion();
            database.close();
            assertTrue(version == ContentDatabase.VERSION);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testRestoreDB() {
        String dbPath = getActivity().getDatabasePath(OnyxStatisticsDatabase.NAME).getPath() + ".db";
        StatisticsUtils.deleteStatisticsList(getActivity());
        List<OnyxStatisticsModel> onyxStatisticsModels =  (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByNote(getActivity(), test);
        assertTrue(onyxStatisticsModels.size() == 0);

        Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap = new HashMap<>();
        String backupFilePath = "mnt/sdcard/" + OnyxStatisticsDatabase.NAME + ".db";
        backupRestoreDBMap.put(DatabaseInfo.create(OnyxStatisticsDatabase.NAME, OnyxStatisticsDatabase.VERSION, dbPath),
                DatabaseInfo.create(backupFilePath));

        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        BackupRestoreDBRequest restoreDBRequest = new BackupRestoreDBRequest(backupRestoreDBMap, false);
        try {
            restoreDBRequest.execute(readerDataHolder.getDataManager());
            initDatabase();
            List<OnyxStatisticsModel> statisticsModels =  (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByNote(getActivity(), test);
            assertTrue(statisticsModels.size() == 1);
            assertTrue(statisticsModels.get(0).getNote().equals(test));

            // test has comment field
            SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null,SQLiteDatabase.OPEN_READWRITE);
            database.execSQL("INSERT INTO OnyxStatisticsModel (comment) VALUES ('test');");
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testRestoreOldDB() {
        String backupFilePath = "mnt/sdcard/" + OnyxStatisticsDatabase.NAME + ".db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(backupFilePath, null,SQLiteDatabase.OPEN_READWRITE);
        // test can auto add comment field by restore
        database.setVersion(1);
        database.execSQL("create table temp as select note, id from OnyxStatisticsModel;");
        database.execSQL("drop table OnyxStatisticsModel;");
        database.execSQL("alter table temp rename to OnyxStatisticsModel; ");
        database.close();
        TestUtils.sleep(2000);
        testRestoreDB();
    }

    private void initDatabase() {
        FlowManager.destroy();
        FlowConfig.Builder builder = new FlowConfig.Builder(getActivity());
        builder.addDatabaseHolder(ReaderNoteGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
    }

}
