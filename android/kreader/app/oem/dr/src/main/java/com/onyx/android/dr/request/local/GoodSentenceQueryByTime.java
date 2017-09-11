package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class GoodSentenceQueryByTime extends BaseDataRequest {
    private final int type;
    private List<GoodSentenceNoteEntity> goodSentenceList = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();
    private long startDateMillisecond;
    private long endDateMillisecond;

    public GoodSentenceQueryByTime(int type, long startDateMillisecond, long endDateMillisecond) {
        this.startDateMillisecond = startDateMillisecond;
        this.endDateMillisecond = endDateMillisecond;
        this.type = type;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryGoodSentenceList();
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public List<GoodSentenceNoteEntity> getData() {
        return goodSentenceList;
    }

    public void queryGoodSentenceList() {
        List<GoodSentenceNoteEntity> essayList = new Select().from(GoodSentenceNoteEntity.class).
                where(GoodSentenceNoteEntity_Table.goodSentenceType.eq(type)).orderBy(GoodSentenceNoteEntity_Table.currentTime, false).queryList();
        if (essayList != null && essayList.size() > 0) {
            listCheck.clear();
            for (int i = 0; i < essayList.size(); i++) {
                GoodSentenceNoteEntity bean = essayList.get(i);
                if (bean.currentTime >= startDateMillisecond &&
                        bean.currentTime <= endDateMillisecond) {
                    if (!goodSentenceList.contains(bean)){
                        goodSentenceList.add(bean);
                        listCheck.add(false);
                    }
                }
            }
        }
    }
}
