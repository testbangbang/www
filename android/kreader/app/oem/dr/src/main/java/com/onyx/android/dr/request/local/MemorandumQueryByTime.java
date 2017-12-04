package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.data.database.MemorandumEntity_Table;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class MemorandumQueryByTime extends BaseDataRequest {
    private List<MemorandumEntity> memorandumList = new ArrayList<>();
    private long startDateMillisecond;
    private long endDateMillisecond;

    public MemorandumQueryByTime(long startDateMillisecond, long endDateMillisecond) {
        this.startDateMillisecond = startDateMillisecond;
        this.endDateMillisecond = endDateMillisecond;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryMemorandumList();
    }

    public List<MemorandumEntity> getAllData() {
        return memorandumList;
    }

    public void setAllData(List<MemorandumEntity> list) {
        this.memorandumList = list;
    }

    public void queryMemorandumList() {
        List<MemorandumEntity> list = new Select().from(MemorandumEntity.class).orderBy(MemorandumEntity_Table.currentTime, false).queryList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                MemorandumEntity bean = list.get(i);
                long currentMillisecond = TimeUtils.getStartDateMillisecond(bean.date);
                if (currentMillisecond >= this.startDateMillisecond &&
                        currentMillisecond <= endDateMillisecond) {
                    if (!memorandumList.contains(bean)){
                        memorandumList.add(bean);
                    }
                }
            }
            setAllData(memorandumList);
        }
    }
}
