package com.onyx.android.dr.reader.requests;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity_Table;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.data.ReadSummaryEntity_Table;
import com.onyx.android.dr.reader.data.ReadSummaryGoodSentenceReviewBean;
import com.onyx.android.dr.reader.data.ReadSummaryNewWordReviewBean;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class RequestReadSummaryQuery extends BaseDataRequest {
    private String readingMatter;
    private String pageNumber;
    private ReadSummaryEntity readSummaryEntity;

    public RequestReadSummaryQuery(String readingMatter, String pageNumber) {
        this.readingMatter = readingMatter;
        this.pageNumber = pageNumber;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryReadSummary();
    }

    private void queryReadSummary() {
        readSummaryEntity = new Select().from(ReadSummaryEntity.class).where(ReadSummaryEntity_Table.bookName.eq(readingMatter)).querySingle();
        if (readSummaryEntity == null) {
            readSummaryEntity = new ReadSummaryEntity();
            readSummaryEntity.bookName = readingMatter;
            readSummaryEntity.pageNumber = pageNumber;
        }
        if (readSummaryEntity != null) {
            readSummaryEntity.newWordList = queryNewWordList();
            readSummaryEntity.goodSentenceList = queryGoodSentenceList();
        }
    }

    private String queryGoodSentenceList() {
        List<ReadSummaryGoodSentenceReviewBean> readSummaryGoodSentenceReviewBeenList = new ArrayList<>();
        List<GoodSentenceNoteEntity> NewWordNoteBookList = new Select().from(GoodSentenceNoteEntity.class).
                where(NewWordNoteBookEntity_Table.readingMatter.eq(readingMatter)).orderBy(NewWordNoteBookEntity_Table.currentTime, false).queryList();
        long startLong = TimeUtils.getTodayStarDateMills();
        long endLong = TimeUtils.getTodayEndDateMills();
        for (GoodSentenceNoteEntity entity : NewWordNoteBookList) {
            if (startLong <= entity.currentTime && endLong >= entity.currentTime) {
                ReadSummaryGoodSentenceReviewBean readSummaryGoodSentenceReviewBean = new ReadSummaryGoodSentenceReviewBean();
                readSummaryGoodSentenceReviewBean.sentence = entity.details;
                readSummaryGoodSentenceReviewBeenList.add(readSummaryGoodSentenceReviewBean);
            }
        }
        return JSON.toJSON(readSummaryGoodSentenceReviewBeenList).toString();
    }

    public ReadSummaryEntity getReadSummaryEntity() {
        return readSummaryEntity;
    }

    public String queryNewWordList() {
        List<ReadSummaryNewWordReviewBean> newWordReviewBeenList = new ArrayList<>();
        List<NewWordNoteBookEntity> NewWordNoteBookList = new Select().from(NewWordNoteBookEntity.class).
                where(NewWordNoteBookEntity_Table.readingMatter.eq(readingMatter)).orderBy(NewWordNoteBookEntity_Table.currentTime, false).queryList();
        long startLong = TimeUtils.getTodayStarDateMills();
        long endLong = TimeUtils.getTodayEndDateMills();
        for (NewWordNoteBookEntity entity : NewWordNoteBookList) {
            if (startLong <= entity.currentTime && endLong >= entity.currentTime) {
                ReadSummaryNewWordReviewBean readSummaryNewWordReviewBean = new ReadSummaryNewWordReviewBean();
                readSummaryNewWordReviewBean.word = entity.newWord;
                newWordReviewBeenList.add(readSummaryNewWordReviewBean);
            }
        }
        return JSON.toJSON(newWordReviewBeenList).toString();
    }
}
