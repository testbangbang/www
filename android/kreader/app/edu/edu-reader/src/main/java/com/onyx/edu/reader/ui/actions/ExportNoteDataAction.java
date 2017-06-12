package com.onyx.edu.reader.ui.actions;

import android.os.Environment;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.request.data.db.CreateDBRequest;
import com.onyx.android.sdk.data.request.data.db.DataRequestChain;
import com.onyx.android.sdk.data.request.data.db.ExportDataToDBRequest;
import com.onyx.android.sdk.reader.host.request.ReaderRequestChain;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.reader.note.model.ReaderNoteDatabase;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import static com.onyx.android.sdk.data.Constant.READER_DATA_FOLDER;

/**
 * Created by ming on 2017/6/2.
 */

public class ExportNoteDataAction extends BaseAction {

    private StringBuffer exportDBFilePath = new StringBuffer();
    private StringBuffer fullFileMd5;

    public ExportNoteDataAction(StringBuffer fullFileMd5) {
        this.fullFileMd5 = fullFileMd5;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        exportDBFilePath.append(Environment.getExternalStorageDirectory().getPath() + "/" + READER_DATA_FOLDER + "/"+ fullFileMd5 +".db");
        FileUtils.deleteFile(exportDBFilePath.toString());
        FileUtils.ensureFileExists(exportDBFilePath.toString());

        DataRequestChain requestChain = new DataRequestChain();
        String documentUniqueId = readerDataHolder.getReader().getDocumentMd5();
        requestChain.addRequest(createExportDBRequest(readerDataHolder), null);
        requestChain.addRequest(exportShapeDataRequest(readerDataHolder, documentUniqueId), null);
        requestChain.addRequest(exportDocumentDataRequest(readerDataHolder, documentUniqueId), baseCallback);
        requestChain.execute(readerDataHolder.getDataManager());
    }

    private BaseDataRequest createExportDBRequest(ReaderDataHolder readerDataHolder) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        return new CreateDBRequest(currentDbPath, exportDBFilePath.toString());
    }

    private BaseDataRequest exportShapeDataRequest(ReaderDataHolder readerDataHolder, String documentUniqueId) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String condition = "documentUniqueId='"+documentUniqueId+"' ";
        String table = "ReaderFormShapeModel";
        return new ExportDataToDBRequest(currentDbPath, exportDBFilePath.toString(), condition, table);

    }

    private BaseDataRequest exportDocumentDataRequest(ReaderDataHolder readerDataHolder, String documentUniqueId) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String condition = "uniqueId='"+documentUniqueId+"' ";
        String table = "ReaderNoteDocumentModel";
        return new ExportDataToDBRequest(currentDbPath, exportDBFilePath.toString(), condition, table);
    }

    public StringBuffer getExportDBFilePath() {
        return exportDBFilePath;
    }
}
