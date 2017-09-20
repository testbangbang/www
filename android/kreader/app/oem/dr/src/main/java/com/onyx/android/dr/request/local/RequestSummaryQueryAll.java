package com.onyx.android.dr.request.local;

import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Collections;
import java.util.List;

/**
 * Created by hehai on 17-1-19.
 */

public class RequestSummaryQueryAll extends BaseDataRequest {
    private List<ReadSummaryEntity> summaryList;
    private final static String TIME = "time";

    public List<ReadSummaryEntity> getSummaryList() {
        return summaryList;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        summaryList = getAllSummary();
    }

    private List<ReadSummaryEntity> getAllSummary() {
        List<ReadSummaryEntity> notes = new Select().from(ReadSummaryEntity.class).orderBy(OrderBy.fromString(TIME)).queryList();
        Collections.reverse(notes);
        return notes;
    }
}
