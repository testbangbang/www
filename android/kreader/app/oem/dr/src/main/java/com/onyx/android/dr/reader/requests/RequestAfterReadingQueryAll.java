package com.onyx.android.dr.reader.requests;

import com.onyx.android.dr.reader.data.AfterReadingEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Collections;
import java.util.List;

/**
 * Created by hehai on 17-1-19.
 */

public class RequestAfterReadingQueryAll extends BaseDataRequest {
    private List<AfterReadingEntity> notes;
    private final static String TIME = "time";

    public RequestAfterReadingQueryAll() {
    }

    public List<AfterReadingEntity> getNotes() {
        return notes;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        notes = getAllNotes();
    }

    public List<AfterReadingEntity> getAllNotes() {
        List<AfterReadingEntity> notes = new Select().from(AfterReadingEntity.class).orderBy(OrderBy.fromString(TIME)).queryList();
        Collections.reverse(notes);
        return notes;
    }
}
