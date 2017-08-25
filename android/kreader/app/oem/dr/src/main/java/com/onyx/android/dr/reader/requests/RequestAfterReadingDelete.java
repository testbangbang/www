package com.onyx.android.dr.reader.requests;


import com.onyx.android.dr.reader.data.AfterReadingEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.List;

/**
 * Created by hehai on 17-1-19.
 */
public class RequestAfterReadingDelete extends BaseDataRequest {
    private List<AfterReadingEntity> notes;

    public RequestAfterReadingDelete(List<AfterReadingEntity> notes) {
        this.notes = notes;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        deleteNote();
    }

    private void deleteNote() {
        for (AfterReadingEntity entity : notes) {
            entity.delete();
        }
    }
}
