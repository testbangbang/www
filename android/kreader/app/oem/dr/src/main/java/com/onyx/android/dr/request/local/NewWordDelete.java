package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class NewWordDelete extends BaseDataRequest {
    private boolean deletePart = false;
    private long currentTime;

    public NewWordDelete() {
    }

    public NewWordDelete(long time, boolean deletePart) {
        this.currentTime = time;
        this.deletePart = deletePart;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (deletePart){
            deleteByTime(currentTime);
        }else{
            clearAllNewWord();
        }
    }

    private void clearAllNewWord() {
        new Delete().from(NewWordNoteBookEntity.class).queryList();
    }

    private void deleteByTime(long time) {
        new Delete().from(NewWordNoteBookEntity.class).where(NewWordNoteBookEntity_Table.currentTime.eq(time)).execute();
    }
}
