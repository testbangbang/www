package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.GetReportListBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/20.
 */

public class GetReportListRequest extends BaseCloudRequest {
    private int courseId;
    private int studentId;
    private GetReportListBean reportList;

    public GetReportListRequest(int courseId, int studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public GetReportListBean getReportList() {
        return reportList;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<GetReportListBean> call = getCall(service);
        Response<GetReportListBean> response = call.execute();
        if (response.isSuccessful()) {
            reportList = response.body();
        }
    }

    private Call<GetReportListBean> getCall(ContentService service) {
        return service.getReportList(courseId, studentId);
    }
}
