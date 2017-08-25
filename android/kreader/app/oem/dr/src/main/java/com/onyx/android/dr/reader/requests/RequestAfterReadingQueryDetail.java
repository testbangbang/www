package com.onyx.android.dr.reader.requests;

import com.onyx.android.dr.reader.data.AfterReadingEntity;
import com.onyx.android.dr.reader.data.AfterReadingEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by hehai on 17-1-19.
 */
public class RequestAfterReadingQueryDetail extends BaseDataRequest {
    private AfterReadingEntity noteEntity;
    private String bookName;

    public RequestAfterReadingQueryDetail(String bookName) {
        this.bookName = bookName;
    }

    public AfterReadingEntity getNoteEntity() {
        return noteEntity;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        noteEntity = getNoteDetail();
    }

    private AfterReadingEntity getNoteDetail() {
        return new Select().from(AfterReadingEntity.class).where(AfterReadingEntity_Table.bookName.eq(bookName)).querySingle();
    }
}
