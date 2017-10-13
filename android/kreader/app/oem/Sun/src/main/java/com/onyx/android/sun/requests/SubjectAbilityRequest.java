package com.onyx.android.sun.requests;

import com.onyx.android.sun.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.sun.cloud.service.ContentService;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.requests.requestTool.BaseCloudRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by hehai on 17-10-9.
 */

public class SubjectAbilityRequest extends BaseCloudRequest {
    private String id;
    private PersonalAbilityResultBean resultBean;

    public SubjectAbilityRequest(String id) {
        this.id = id;
    }

    public PersonalAbilityResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        executeCloudRequest();
    }

    private void executeCloudRequest() {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<PersonalAbilityResultBean> call = getCall(service);
            Response<PersonalAbilityResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }
        } catch (Exception e) {
            setException(e);
        }
    }

    private Call<PersonalAbilityResultBean> getCall(ContentService service) {
        return service.getSubjectAbility(id);
    }
}
