package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.request.data.db.CreateDBRequest;
import com.onyx.android.sdk.data.request.data.db.DataRequestChain;
import com.onyx.android.sdk.data.request.data.db.ExportDataToDBRequest;
import com.onyx.android.sdk.reader.host.request.ReaderRequestChain;
import com.onyx.edu.reader.note.model.ReaderNoteDatabase;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/2.
 */

public class ExportNoteDataAction extends BaseAction {

    private String exportDBFilePath;

    public ExportNoteDataAction(String exportDBFilePath) {
        this.exportDBFilePath = exportDBFilePath;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        DataRequestChain requestChain = new DataRequestChain();
        String documentUniqueId = readerDataHolder.getReader().getDocumentMd5();
        requestChain.addRequest(createExportDBRequest(readerDataHolder), null);
        requestChain.addRequest(exportShapeDataRequest(readerDataHolder, documentUniqueId), null);
        requestChain.addRequest(exportDocumentDataRequest(readerDataHolder, documentUniqueId), baseCallback);
        requestChain.execute(readerDataHolder.getDataManager());
    }

    private BaseDataRequest createExportDBRequest(ReaderDataHolder readerDataHolder) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        return new CreateDBRequest(currentDbPath, exportDBFilePath);
    }

    private BaseDataRequest exportShapeDataRequest(ReaderDataHolder readerDataHolder, String documentUniqueId) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String condition = "documentUniqueId='"+documentUniqueId+"' ";
        String table = "ReaderFormShapeModel";
        return new ExportDataToDBRequest(currentDbPath, exportDBFilePath, condition, table);

    }

    private BaseDataRequest exportDocumentDataRequest(ReaderDataHolder readerDataHolder, String documentUniqueId) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String condition = "uniqueId='"+documentUniqueId+"' ";
        String table = "ReaderNoteDocumentModel";
        return new ExportDataToDBRequest(currentDbPath, exportDBFilePath, condition, table);
    }
}
