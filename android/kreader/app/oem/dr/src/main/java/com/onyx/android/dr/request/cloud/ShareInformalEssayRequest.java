package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.event.ShareFailedEvent;
import com.onyx.android.dr.event.ShareSuccessEvent;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.ShareBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.ShareBookReportResult;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by li on 2017/9/27.
 */

public class ShareInformalEssayRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private ShareBookReportRequestBean requestBean;
    private String id;
    private List<ShareBookReportResult> result = new ArrayList<>();

    public ShareInformalEssayRequest(String libraryId, ShareBookReportRequestBean bean) {
        this.id = libraryId;
        this.requestBean = bean;

    }
    @Override
    public void execute(CloudManager parent) {
        Response<List<ShareBookReportResult>> response = null;
        try {
            response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .shareInformalEssay(id, requestBean));
            EventBus.getDefault().post(new ShareSuccessEvent());
        } catch (Exception e) {
            EventBus.getDefault().post(new ShareFailedEvent());
            e.printStackTrace();
        }
        if(response != null) {
            result = response.body();
        }
    }

    public List<ShareBookReportResult> getResult() {
        return result;
    }
}
