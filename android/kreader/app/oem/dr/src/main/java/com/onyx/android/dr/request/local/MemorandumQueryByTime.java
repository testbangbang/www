package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.data.database.MemorandumEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class MemorandumQueryByTime extends BaseDataRequest {
    private List<MemorandumEntity> memorandumList;
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
        List<MemorandumEntity> memorandumList = new Select().from(MemorandumEntity.class).orderBy(MemorandumEntity_Table.currentTime, false).queryList();
        if (memorandumList != null && memorandumList.size() > 0) {
            for (int i = 0; i < memorandumList.size(); i++) {
                MemorandumEntity bean = memorandumList.get(i);
                if (bean.currentTime >= startDateMillisecond &&
                        bean.currentTime <= endDateMillisecond) {
                    if (!memorandumList.contains(bean)){
                        memorandumList.add(bean);
                    }
                }
            }
            setAllData(memorandumList);
        }
    }
}
