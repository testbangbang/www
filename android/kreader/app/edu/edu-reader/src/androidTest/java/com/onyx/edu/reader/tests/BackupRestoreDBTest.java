package com.onyx.edu.reader.tests;

import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.onyx.android.sdk.data.DatabaseInfo;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.request.data.db.TransferDBRequest;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/4/24.
 */

public class BackupRestoreDBTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public BackupRestoreDBTest() {
        super(ReaderTestActivity.class);
    }

    private static final String test = "ashjbfhjasbfhjsabfjhbsajfbsafa";

    public void testBackupDB() {
        StatisticsUtils.deleteStatisticsListByNote(getActivity(), test);
        OnyxStatisticsModel statisticsModel = new OnyxStatisticsModel();
        statisticsModel.setNote(test);
        statisticsModel.save();
        Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap = new HashMap<>();
        final String backupDBPath = "mnt/sdcard/" + OnyxStatisticsDatabase.NAME + ".db";
        backupRestoreDBMap.put(DatabaseInfo.create(OnyxStatisticsDatabase.NAME, OnyxStatisticsDatabase.VERSION, getActivity().getDatabasePath(OnyxStatisticsDatabase.NAME).getPath() + ".db"),
                DatabaseInfo.create(backupDBPath));

        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        TransferDBRequest restoreDBRequest = new TransferDBRequest(backupRestoreDBMap, true);
        try {
            restoreDBRequest.execute(readerDataHolder.getDataManager());
            SQLiteDatabase database = SQLiteDatabase.openDatabase(backupDBPath, null,SQLiteDatabase.OPEN_READWRITE);
            int version = database.getVersion();
            database.close();
            assertTrue(version == ContentDatabase.VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testRestoreDB() {
        StatisticsUtils.deleteStatisticsListByNote(getActivity(), test);
        List<OnyxStatisticsModel> onyxStatisticsModels =  (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByNote(getActivity(), test);
        assertTrue(onyxStatisticsModels.size() == 0);

        Map<DatabaseInfo, DatabaseInfo> backupRestoreDBMap = new HashMap<>();
        backupRestoreDBMap.put(DatabaseInfo.create(OnyxStatisticsDatabase.NAME, OnyxStatisticsDatabase.VERSION, getActivity().getDatabasePath(OnyxStatisticsDatabase.NAME).getPath() + ".db"),
                DatabaseInfo.create("mnt/sdcard/" + OnyxStatisticsDatabase.NAME + ".db"));
        ReaderDataHolder readerDataHolder = new ReaderDataHolder(getActivity());
        TransferDBRequest restoreDBRequest = new TransferDBRequest(backupRestoreDBMap, false);
        try {
            restoreDBRequest.execute(readerDataHolder.getDataManager());
            List<OnyxStatisticsModel> statisticsModels =  (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByNote(getActivity(), test);
            assertTrue(statisticsModels.size() == 1);
            assertTrue(statisticsModels.get(0).getNote().equals(test));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
