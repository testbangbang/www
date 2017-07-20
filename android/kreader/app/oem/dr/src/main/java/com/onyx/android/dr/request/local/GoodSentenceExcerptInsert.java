package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class GoodSentenceExcerptInsert extends BaseDataRequest {
    private GoodSentenceNoteEntity goodSentencesInfo;
    private GoodSentenceNoteEntity goodSentenceEntity;
    private boolean weatherInsert = true;

    public GoodSentenceExcerptInsert(GoodSentenceNoteEntity goodSentenceNoteEntity) {
        this.goodSentencesInfo = goodSentenceNoteEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        insertData();
    }

    private void insertData() {
        if (!whetherInsert()) {
            goodSentencesInfo.insert();
        } else {
            goodSentenceEntity.week = goodSentencesInfo.week;
            goodSentenceEntity.month = goodSentencesInfo.month;
            goodSentenceEntity.day = goodSentencesInfo.day;
            goodSentenceEntity.currentTime = goodSentencesInfo.currentTime;
            goodSentenceEntity.details = goodSentencesInfo.details;
            goodSentenceEntity.readingMatter = goodSentencesInfo.readingMatter;
            goodSentenceEntity.pageNumber = goodSentencesInfo.pageNumber;
            goodSentenceEntity.update();
        }
    }

    public boolean whetherInsert() {
        List<GoodSentenceNoteEntity> dataList = queryGoodSentenceList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                goodSentenceEntity = dataList.get(i);
                if (goodSentencesInfo.details.equals(goodSentenceEntity.details)) {
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

    public List<GoodSentenceNoteEntity> queryGoodSentenceList() {
        List<GoodSentenceNoteEntity> dataList = new Select().from(GoodSentenceNoteEntity.class).queryList();
        return dataList;
    }
}
