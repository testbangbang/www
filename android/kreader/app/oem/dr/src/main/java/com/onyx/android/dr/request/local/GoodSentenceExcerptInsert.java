package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class GoodSentenceExcerptInsert extends BaseDataRequest {
    private GoodSentenceNoteEntity goodSentencesInfo;

    public GoodSentenceExcerptInsert(GoodSentenceNoteEntity goodSentenceNoteEntity) {
        this.goodSentencesInfo = goodSentenceNoteEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        goodSentencesInfo.insert();
    }
}
