package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.manager.OperatingDataManager;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;
import com.onyx.android.sdk.data.model.v2.GetSharedInformalResult;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by li on 2017/9/28.
 */

public class GetSharedInformalRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private String libraryId;
    private GetBookReportListRequestBean requestBean;
    private GetSharedInformalResult result;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public GetSharedInformalRequest(String libraryId, GetBookReportListRequestBean requstBean) {
        this.requestBean = requstBean;
        this.libraryId = libraryId;
    }

    public GetSharedInformalResult getResult() {
        return result;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<GetSharedInformalResult> response = executeCall(ServiceFactory.getContentService(parent.
                getCloudConf().getApiBase()).getSharedInformalEssay(libraryId, requestBean.offset,
                requestBean.limit, requestBean.sortBy, requestBean.order));
        listCheck.clear();
        if (response != null) {
            result = response.body();
            List<CreateInformalEssayBean> list = result.list;
            if (list != null && list.size() > 0) {
                for (int i = 0; i <list.size(); i++) {
                    listCheck.add(false);
                    OperatingDataManager.getInstance().insertInformalEssay(list.get(i));
                }
            }
        }
    }
}
