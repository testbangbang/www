package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class GoodSentenceDeleteByTime extends BaseDataRequest {
    private long currentTime;

    public GoodSentenceDeleteByTime(long time) {
        this.currentTime = time;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        deleteByTime(currentTime);
    }

    private void deleteByTime(long time) {
        new Delete().from(GoodSentenceNoteEntity.class).where(GoodSentenceNoteEntity_Table.currentTime.eq(time)).execute();
    }
}
