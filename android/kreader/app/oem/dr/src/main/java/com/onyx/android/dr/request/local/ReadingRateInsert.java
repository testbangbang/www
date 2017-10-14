package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class ReadingRateInsert extends BaseDataRequest {
    private ReadingRateEntity readingRateInfo;
    private ReadingRateEntity readingRateEntity;
    private boolean weatherInsert = true;

    public ReadingRateInsert(ReadingRateEntity readingRateEntity) {
        this.readingRateInfo = readingRateEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        insertData();
    }

    private void insertData() {
        if (!whetherInsert()) {
            readingRateInfo.insert();
        } else {
            readingRateEntity.recordDate = readingRateInfo.recordDate;
            readingRateEntity.book = readingRateInfo.book;
            readingRateEntity.readTimeLong = readingRateInfo.readTimeLong;
            readingRateEntity.wordsCount = readingRateInfo.wordsCount;
            readingRateEntity.language = readingRateInfo.language;
            readingRateEntity.speed = readingRateInfo.speed;
            readingRateEntity.summaryCount = readingRateInfo.summaryCount;
            readingRateEntity.impressionCount = readingRateInfo.impressionCount;
            readingRateEntity.impressionWordsCount = readingRateInfo.impressionWordsCount;
            readingRateEntity.cloudId = readingRateInfo.cloudId;
            readingRateEntity.update();
        }
    }

    public boolean whetherInsert() {
        List<ReadingRateEntity> dataList = queryList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                readingRateEntity = dataList.get(i);
                if (readingRateInfo.name.equals(readingRateEntity.name)) {
                    weatherInsert = false;
                    return true;
                }
            }
            if (weatherInsert) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public List<ReadingRateEntity> queryList() {
        List<ReadingRateEntity> dataList = new Select().from(ReadingRateEntity.class).queryList();
        return dataList;
    }
}
