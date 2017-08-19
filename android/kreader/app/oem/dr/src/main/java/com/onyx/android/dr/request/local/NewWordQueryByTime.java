package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.util.SortClass;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class NewWordQueryByTime extends BaseDataRequest {
    private List<NewWordNoteBookEntity> list = new ArrayList<>();
    private long startDateMillisecond;
    private long endDateMillisecond;

    public NewWordQueryByTime(long startDateMillisecond, long endDateMillisecond) {
        this.startDateMillisecond = startDateMillisecond;
        this.endDateMillisecond = endDateMillisecond;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryGoodSentenceList();
    }

    public List<NewWordNoteBookEntity> getData() {
        SortClass sort = new SortClass();
        Collections.sort(list, sort);
        return list;
    }

    public void queryGoodSentenceList() {
        List<NewWordNoteBookEntity> essayList = new Select().from(NewWordNoteBookEntity.class).queryList();
        if (essayList != null && essayList.size() > 0) {
            for (int i = 0; i < essayList.size(); i++) {
                NewWordNoteBookEntity bean = essayList.get(i);
                if (bean.currentTime >= startDateMillisecond &&
                        bean.currentTime <= endDateMillisecond) {
                    if (!list.contains(bean)){
                        list.add(bean);
                    }
                }
            }
        }
    }
}
