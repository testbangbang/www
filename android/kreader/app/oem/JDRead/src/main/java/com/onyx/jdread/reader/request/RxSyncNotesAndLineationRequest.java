package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.reader.data.SyncNotesAndLineationRequestBean;
import com.onyx.jdread.reader.data.SyncNotesResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/3/20.
 */

public class RxSyncNotesAndLineationRequest extends RxBaseCloudRequest {
    private SyncNotesAndLineationRequestBean requestBean;
    private SyncNotesResultBean resultBean;

    public RxSyncNotesAndLineationRequest(SyncNotesAndLineationRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<SyncNotesResultBean> call = getCall(service);
        Response<SyncNotesResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<SyncNotesResultBean> getCall(ReadContentService service) {
        return service.syncNoteAndLineation(requestBean.bookId, requestBean.baseInfoMap, requestBean.body);
    }

    public SyncNotesResultBean getResultBean() {
        return resultBean;
    }
}
