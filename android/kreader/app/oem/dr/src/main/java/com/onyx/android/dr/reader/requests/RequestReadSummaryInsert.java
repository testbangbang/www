package com.onyx.android.dr.reader.requests;

import com.onyx.android.dr.event.InsertReadSummarySuccessEvent;
import com.onyx.android.dr.event.UpdateReadSummaryEvent;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.data.ReadSummaryEntity_Table;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.greenrobot.eventbus.EventBus;

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
            readSummaryEntity.time = TimeUtils.getDate(System.currentTimeMillis());
            entity.update();
            EventBus.getDefault().post(new UpdateReadSummaryEvent());
        } else {
            readSummaryEntity.time = TimeUtils.getDate(System.currentTimeMillis());
            readSummaryEntity.insert();
            EventBus.getDefault().post(new InsertReadSummarySuccessEvent());
        }
    }
}
