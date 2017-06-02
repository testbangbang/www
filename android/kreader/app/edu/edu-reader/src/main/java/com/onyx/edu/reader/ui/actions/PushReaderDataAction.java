package com.onyx.edu.reader.ui.actions;

import android.os.Environment;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.request.cloud.PushReaderDataRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.reader.note.model.ReaderNoteDatabase;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import static com.onyx.android.sdk.data.Constant.READER_DATA_FOLDER;

/**
 * Created by ming on 2017/5/31.
 */

public class PushReaderDataAction extends BaseAction {

    private String exportDBFilePath;

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {

        final GetFileFullMd5Action getFileFullMd5Action = getFileFullMd5(readerDataHolder);
        getFileFullMd5Action.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                exportReaderDataToDB(readerDataHolder, getFileFullMd5Action.getFullFileMd5(), baseCallback);
            }
        });

    }

    private void exportReaderDataToDB(final ReaderDataHolder readerDataHolder, final String fullFileMd5, final BaseCallback baseCallback) {
        exportDBFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + READER_DATA_FOLDER + "/"+ fullFileMd5 +".db";
        FileUtils.deleteFile(exportDBFilePath);
        FileUtils.ensureFileExists(exportDBFilePath);

        final ActionChain actionChain = new ActionChain();
        String documentUniqueId = readerDataHolder.getReader().getDocumentMd5();
        actionChain.addAction(createExportDB(readerDataHolder));
        actionChain.addAction(exportShapeData(readerDataHolder, documentUniqueId));
        actionChain.addAction(exportDocumentData(readerDataHolder, documentUniqueId));
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                pushReaderData(readerDataHolder, baseCallback, fullFileMd5);
            }
        });
    }

    private void pushReaderData(ReaderDataHolder readerDataHolder, BaseCallback baseCallback, String fileFullMd5) {
        // TODO: 2017/6/1 for test
        String fileID = "592e8bde6ecae4b9299b4cd4";
        PushReaderDataRequest pushReaderDataRequest = new PushReaderDataRequest(exportDBFilePath, readerDataHolder.getContext(), Constant.SYNC_API_BASE, fileFullMd5, fileID);
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), pushReaderDataRequest, baseCallback);
    }

    private GetFileFullMd5Action getFileFullMd5(ReaderDataHolder readerDataHolder) {
        String filePath = readerDataHolder.getDocumentPath();
        return new GetFileFullMd5Action(filePath);
    }

    private CreateDBAction createExportDB(ReaderDataHolder readerDataHolder) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        return new CreateDBAction(currentDbPath, exportDBFilePath);
    }

    private ExportDataToDBAction exportShapeData(ReaderDataHolder readerDataHolder, String documentUniqueId) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String condition = "documentUniqueId='"+documentUniqueId+"' ";
        String table = "ReaderNoteShapeModel";
        return new ExportDataToDBAction(currentDbPath, exportDBFilePath, condition, table);
    }

    private ExportDataToDBAction exportDocumentData(ReaderDataHolder readerDataHolder, String documentUniqueId) {
        String currentDbPath = readerDataHolder.getContext().getDatabasePath(ReaderNoteDatabase.NAME).getPath() + ".db";
        String condition = "uniqueId='"+documentUniqueId+"' ";
        String table = "ReaderNoteDocumentModel";
        return new ExportDataToDBAction(currentDbPath, exportDBFilePath, condition, table);
    }


}
