package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class NewWordQueryByTime extends BaseDataRequest {
    private final int type;
    private List<NewWordNoteBookEntity> list = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();
    private long startDateMillisecond;
    private long endDateMillisecond;

    public NewWordQueryByTime(int type, long startDateMillisecond, long endDateMillisecond) {
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

    public List<NewWordNoteBookEntity> getData() {
        return list;
    }

    public void queryGoodSentenceList() {
        List<NewWordNoteBookEntity> essayList = new Select().from(NewWordNoteBookEntity.class).
                where(NewWordNoteBookEntity_Table.newWordType.eq(type)).orderBy(NewWordNoteBookEntity_Table.currentTime, false).queryList();
        if (essayList != null && essayList.size() > 0) {
            listCheck.clear();
            for (int i = 0; i < essayList.size(); i++) {
                NewWordNoteBookEntity bean = essayList.get(i);
                if (bean.currentTime >= startDateMillisecond &&
                        bean.currentTime <= endDateMillisecond) {
                    if (!list.contains(bean)){
                        list.add(bean);
                        listCheck.add(false);
                    }
                }
            }
        }
    }
}
