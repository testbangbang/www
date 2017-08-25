package com.onyx.android.dr.reader.requests;

import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.data.ReadSummaryEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by hehai on 17-8-24.
 */

public class RequestReadSummaryInsert extends BaseDataRequest {
    private ReadSummaryEntity readSummaryEntity;

    public RequestReadSummaryInsert(ReadSummaryEntity readSummaryEntity) {
        this.readSummaryEntity = readSummaryEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        ReadSummaryEntity entity = new Select().from(ReadSummaryEntity.class).where(ReadSummaryEntity_Table.bookName.eq(this.readSummaryEntity.bookName)).and(ReadSummaryEntity_Table.pageNumber.eq(this.readSummaryEntity.pageNumber)).querySingle();
        if (entity != null) {
            entity.summary = readSummaryEntity.summary;
            entity.newWordList = readSummaryEntity.newWordList;
            entity.goodSentenceList = readSummaryEntity.goodSentenceList;
            entity.update();
        } else {
            readSummaryEntity.insert();
        }
    }
}
