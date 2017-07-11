package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class NewWordDelete extends BaseDataRequest {

    public NewWordDelete() {
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        clearNewWord();
    }

    private void clearNewWord() {
        new Delete().from(NewWordNoteBookEntity.class).queryList();
    }
}
