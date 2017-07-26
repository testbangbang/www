package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.data.database.InformalEssayEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class InformalEssayDelete extends BaseDataRequest {
    private boolean deletePart = false;
    private long currentTime;

    public InformalEssayDelete() {
    }

    public InformalEssayDelete(long time, boolean deletePart) {
        this.currentTime = time;
        this.deletePart = deletePart;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (deletePart){
            deleteByTime(currentTime);
        }else{
            clearAllData();
        }
    }

    private void clearAllData() {
        new Delete().from(InformalEssayEntity.class).queryList();
    }

    private void deleteByTime(long time) {
        new Delete().from(InformalEssayEntity.class).where(InformalEssayEntity_Table.currentTime.eq(time)).execute();
    }
}
