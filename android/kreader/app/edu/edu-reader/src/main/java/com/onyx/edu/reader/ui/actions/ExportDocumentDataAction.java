package com.onyx.edu.reader.ui.actions;

import android.os.Environment;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.request.cloud.SaveDocumentDataToCloudRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import static com.onyx.android.sdk.data.Constant.READER_DATA_FOLDER;

/**
 * Created by ming on 2017/5/31.
 */

public class ExportDocumentDataAction extends BaseAction {

    private String exportDBFilePath;

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {

        final GetFileFullMd5Action getFileFullMd5Action = getFileFullMd5(readerDataHolder);
        getFileFullMd5Action.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                exportDocumentDataToDB(readerDataHolder, getFileFullMd5Action.getFullFileMd5(), baseCallback);
            }
        });

    }

    private void exportDocumentDataToDB(final ReaderDataHolder readerDataHolder, final String fullFileMd5, final BaseCallback baseCallback) {
        exportDBFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + READER_DATA_FOLDER + "/"+ fullFileMd5 +".db";
        FileUtils.deleteFile(exportDBFilePath);
        FileUtils.ensureFileExists(exportDBFilePath);

        new ExportNoteDataAction(exportDBFilePath).execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                pushDocumentData(readerDataHolder, baseCallback, fullFileMd5);
            }
        });
    }

    private void pushDocumentData(ReaderDataHolder readerDataHolder, BaseCallback baseCallback, String fileFullMd5) {
        // TODO: 2017/6/1 for test
        String fileID = "592e8bde6ecae4b9299b4cd4";
        SaveDocumentDataToCloudRequest saveDocumentDataToCloudRequest = new SaveDocumentDataToCloudRequest(exportDBFilePath, readerDataHolder.getContext(), Constant.SYNC_API_BASE, fileFullMd5, fileID);
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), saveDocumentDataToCloudRequest, baseCallback);
    }

    private GetFileFullMd5Action getFileFullMd5(ReaderDataHolder readerDataHolder) {
        String filePath = readerDataHolder.getDocumentPath();
        return new GetFileFullMd5Action(filePath);
    }
}
