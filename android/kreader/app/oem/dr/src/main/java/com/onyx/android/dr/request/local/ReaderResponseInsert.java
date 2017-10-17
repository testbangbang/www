package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.ReaderResponseEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/10/16.
 */
public class ReaderResponseInsert extends BaseDataRequest {
    private ReaderResponseEntity readingRateInfo;
    private ReaderResponseEntity readingRateEntity;
    private boolean weatherInsert = true;

    public ReaderResponseInsert(ReaderResponseEntity readingRateEntity) {
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
            readingRateEntity.bookName = readingRateInfo.bookName;
            readingRateEntity.wordNumber = readingRateInfo.wordNumber;
            readingRateEntity.bookId = readingRateInfo.bookId;
            readingRateEntity.updatedAt = readingRateInfo.updatedAt;
            readingRateEntity.createdAt = readingRateInfo.createdAt;
            readingRateEntity.cloudId = readingRateInfo.cloudId;
            readingRateEntity.pageNumber = readingRateInfo.pageNumber;
            readingRateEntity.user = readingRateInfo.user;
            readingRateEntity.update();
        }
    }

    public boolean whetherInsert() {
        List<ReaderResponseEntity> dataList = queryList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                readingRateEntity = dataList.get(i);
                if (readingRateInfo.title.equals(readingRateEntity.title)
                        && readingRateInfo.content.equals(readingRateEntity.content)) {
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

    public List<ReaderResponseEntity> queryList() {
        List<ReaderResponseEntity> dataList = new Select().from(ReaderResponseEntity.class).queryList();
        return dataList;
    }
}
