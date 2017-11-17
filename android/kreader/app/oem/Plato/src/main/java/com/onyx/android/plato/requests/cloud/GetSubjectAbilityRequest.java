package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.GetSubjectAbilityRequestBean;
import com.onyx.android.plato.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/2.
 */

public class GetSubjectAbilityRequest extends BaseCloudRequest {
    private GetSubjectAbilityRequestBean requestBean;
    private GetSubjectAbilityResultBean resultBean;

    public GetSubjectAbilityRequest(GetSubjectAbilityRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public GetSubjectAbilityResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        executeCloudRequest();
    }

    private void executeCloudRequest() {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<GetSubjectAbilityResultBean> call = getCall(service);
            Response<GetSubjectAbilityResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }
        } catch (Exception e) {
            setException(e);
        }
    }

    private Call<GetSubjectAbilityResultBean> getCall(ContentService service) {
        return service.getSubjectAbility(requestBean.id, requestBean.course, requestBean.term);
    }
}
