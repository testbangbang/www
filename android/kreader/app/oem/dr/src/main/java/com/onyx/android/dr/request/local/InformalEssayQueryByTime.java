package com.onyx.android.dr.request.local;

import android.util.Log;

import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class InformalEssayQueryByTime extends BaseDataRequest {
    private List<InformalEssayEntity> informalEssayList = new ArrayList<>();
    private long startDateMillisecond;
    private long endDateMillisecond;

    public InformalEssayQueryByTime(long startDateMillisecond, long endDateMillisecond) {
        this.startDateMillisecond = startDateMillisecond;
        this.endDateMillisecond = endDateMillisecond;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryInformalEssayList();
    }

    public List<InformalEssayEntity> getData() {
        return informalEssayList;
    }

    public void queryInformalEssayList() {
        List<InformalEssayEntity> essayList = new Select().from(InformalEssayEntity.class).queryList();
        if (essayList != null && essayList.size() > 0) {
            for (int i = 0; i < essayList.size(); i++) {
                InformalEssayEntity informalEssayEntity = essayList.get(i);
                if (informalEssayEntity.currentTime >= startDateMillisecond &&
                        informalEssayEntity.currentTime <= endDateMillisecond) {
                    if (!informalEssayList.contains(informalEssayEntity)){
                        informalEssayList.add(informalEssayEntity);
                    }
                }
            }
        }
    }
}
