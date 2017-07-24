package com.onyx.kreader.tests;

import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.request.data.db.TransferDBRequest;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ReaderNoteGeneratedDatabaseHolder;

import java.util.List;

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
        String currentDBPath = getActivity().getDatabasePath(OnyxStatisticsDatabase.NAME).getPath()+ ".db";
        FileUtils.deleteFile(currentDBPath);
        initDatabase();
        StatisticsUtils.deleteStatisticsList(getActivity());
        OnyxStatisticsModel statisticsModel = new OnyxStatisticsModel();
        statisticsModel.setNote(test);
        statisticsModel.save();
        final String backupDBPath = "mnt/sdcard/" + OnyxStatisticsDatabase.NAME + ".db";
        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        TransferDBRequest restoreDBRequest = new TransferDBRequest(currentDBPath, backupDBPath, false, false, null);
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
        String currentDBPath = getActivity().getDatabasePath(OnyxStatisticsDatabase.NAME).getPath() + ".db";
        StatisticsUtils.deleteStatisticsList(getActivity());
        List<OnyxStatisticsModel> onyxStatisticsModels =  (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByNote(getActivity(), test);
        assertTrue(onyxStatisticsModels.size() == 0);

        String backupFilePath = "mnt/sdcard/" + OnyxStatisticsDatabase.NAME + ".db";
        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());

        TransferDBRequest restoreDBRequest = new TransferDBRequest(backupFilePath, currentDBPath, true, true, ReaderNoteGeneratedDatabaseHolder.class);
        try {
            restoreDBRequest.execute(readerDataHolder.getDataManager());
            initDatabase();
            List<OnyxStatisticsModel> statisticsModels =  (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByNote(getActivity(), test);
            assertTrue(statisticsModels.size() == 1);
            assertTrue(statisticsModels.get(0).getNote().equals(test));

            // test has comment field
            SQLiteDatabase database = SQLiteDatabase.openDatabase(currentDBPath, null,SQLiteDatabase.OPEN_READWRITE);
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
