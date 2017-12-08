package com.onyx.android.plato.requests.cloud;

import android.util.Log;

import com.onyx.android.plato.cloud.bean.GetStudyReportDetailResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 *Created by jackdeng on 2017/10/26.
 */

public class GetStudyReportDetailRequest extends BaseCloudRequest {
    private final static String TAG = GetStudyReportDetailRequest.class.getSimpleName();
    private int id;
    private GetStudyReportDetailResultBean studyReportDetailResultBean;
    private String errorBody;

    public GetStudyReportDetailRequest(int id) {
        this.id = id;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public GetStudyReportDetailResultBean getStudyReportDetailResultBean() {
        return studyReportDetailResultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<GetStudyReportDetailResultBean> call = getCall(service);
            Response<GetStudyReportDetailResultBean> response = call.execute();
            if (response.isSuccessful()) {
                studyReportDetailResultBean = response.body();
            } else {
                errorBody = response.errorBody().string();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            setException(e);
        }
    }

    private Call<GetStudyReportDetailResultBean> getCall(ContentService service) {
        return service.getStudyReportDetail(id);
    }
}
