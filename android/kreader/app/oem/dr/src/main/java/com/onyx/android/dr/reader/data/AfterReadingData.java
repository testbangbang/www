package com.onyx.android.dr.reader.data;


import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.reader.requests.RequestAfterReadingDelete;
import com.onyx.android.dr.reader.requests.RequestAfterReadingInsert;
import com.onyx.android.dr.reader.requests.RequestAfterReadingQueryAll;
import com.onyx.android.dr.reader.requests.RequestAfterReadingQueryDetail;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by hehai on 17-1-19.
 */
public class AfterReadingData {
    public void getAllAfterReadings(final RequestAfterReadingQueryAll req, BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void getAfterReadingDetail(final RequestAfterReadingQueryDetail req, BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void insertAfterReading(final RequestAfterReadingInsert req, BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void deleteAfterReading(final RequestAfterReadingDelete req, BaseCallback callback) {
        submitRequest(req, callback);
    }

    private void submitRequest(final BaseDataRequest baseRequest, final BaseCallback callback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance().getBaseContext(), baseRequest, callback);
    }
}
