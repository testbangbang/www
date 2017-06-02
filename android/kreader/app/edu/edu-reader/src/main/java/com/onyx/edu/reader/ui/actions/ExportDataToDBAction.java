package com.onyx.edu.reader.ui.actions;

import android.os.Environment;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.data.db.ExportDBRequest;
import com.onyx.android.sdk.data.request.data.db.ExportDataToDBRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import static com.onyx.android.sdk.data.Constant.READER_DATA_FOLDER;

/**
 * Created by ming on 2017/5/31.
 */

public class ExportDataToDBAction extends BaseAction {

    private String currentDbPath;
    private String exportDbPath;
    private String exportCondition;
    private String exportTable;

    public ExportDataToDBAction(String currentDbPath, String exportDbPath, String exportCondition, String exportTable) {
        this.currentDbPath = currentDbPath;
        this.exportDbPath = exportDbPath;
        this.exportCondition = exportCondition;
        this.exportTable = exportTable;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        ExportDataToDBRequest exportDataToDBRequest = new ExportDataToDBRequest(currentDbPath, exportDbPath, exportCondition, exportTable);
        readerDataHolder.getDataManager().submit(readerDataHolder.getContext(), exportDataToDBRequest, baseCallback);
    }
}
