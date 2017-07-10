package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class NewWordInsert extends BaseDataRequest {
    private NewWordNoteBookEntity newWordsInfo;

    public NewWordInsert(NewWordNoteBookEntity newWordNoteBookEntity) {
        this.newWordsInfo = newWordNoteBookEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        newWordsInfo.insert();
    }
}
