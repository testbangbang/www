package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class GoodSentenceExcerptDelete extends BaseDataRequest {

    public GoodSentenceExcerptDelete() {
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        clearGoodSentence();
    }

    private void clearGoodSentence() {
        new Delete().from(GoodSentenceNoteEntity.class).queryList();
    }
}
