package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.data.db.CreateDBRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/1.
 */

public class CreateDBAction extends BaseAction {

    private String currentDbPath;
    private String createDbPath;

    public CreateDBAction(String currentDbPath, String createDbPath) {
        this.currentDbPath = currentDbPath;
        this.createDbPath = createDbPath;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        CreateDBRequest createDBRequest = new CreateDBRequest(currentDbPath, createDbPath);
        readerDataHolder.getDataManager().submit(readerDataHolder.getContext(), createDBRequest, baseCallback);
    }

}
