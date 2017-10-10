package com.onyx.android.dr.request.local;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.data.database.ReadingRateEntity_Table;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class ReadingRateQueryByTimeAndType extends BaseDataRequest {
    private final String language;
    private List<ReadingRateEntity> list = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();
    private long startDateMillisecond;
    private long endDateMillisecond;

    public ReadingRateQueryByTimeAndType(String language, long startDateMillisecond, long endDateMillisecond) {
        this.startDateMillisecond = startDateMillisecond;
        this.endDateMillisecond = endDateMillisecond;
        this.language = language;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryGoodSentenceList();
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public List<ReadingRateEntity> getData() {
        return list;
    }

    public void queryGoodSentenceList() {
        if (language.equals(DRApplication.getInstance().getString(R.string.all))) {
            List<ReadingRateEntity> essayList = new Select().from(ReadingRateEntity.class).queryList();
            if (essayList != null && essayList.size() > 0) {
                listCheck.clear();
                for (int i = 0; i < essayList.size(); i++) {
                    ReadingRateEntity bean = essayList.get(i);
                    long currentTime = TimeUtils.dateToLong(bean.recordDate);
                    if (currentTime >= startDateMillisecond &&
                            currentTime <= endDateMillisecond) {
                        if (!list.contains(bean)){
                            list.add(bean);
                            listCheck.add(false);
                        }
                    }
                }
            }
        } else {
            List<ReadingRateEntity> essayList = new Select().from(ReadingRateEntity.class).
                    where(ReadingRateEntity_Table.language.eq(language)).queryList();
            if (essayList != null && essayList.size() > 0) {
                listCheck.clear();
                for (int i = 0; i < essayList.size(); i++) {
                    ReadingRateEntity bean = essayList.get(i);
                    long currentTime = TimeUtils.dateToLong(bean.recordDate);
                    if (currentTime >= startDateMillisecond &&
                            currentTime <= endDateMillisecond) {
                        if (!list.contains(bean)){
                            list.add(bean);
                            listCheck.add(false);
                        }
                    }
                }
            }
        }
    }
}
