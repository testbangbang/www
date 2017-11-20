package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CreateReadingRateBean;
import com.onyx.android.sdk.data.model.v2.GetReadingRateBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by li on 2017/9/28.
 */

public class GetSharedReadingRateRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private String libraryId;
    private  List<CreateReadingRateBean> result;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public GetSharedReadingRateRequest(String libraryId) {
        this.libraryId = libraryId;
    }

    public  List<CreateReadingRateBean> getResult() {
        return result;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<GetReadingRateBean> response = executeCall(ServiceFactory.getContentService(parent.
                getCloudConf().getApiBase()).getSharedReadRecords(libraryId));
        listCheck.clear();
        if (response != null) {
            result = response.body().list;
            if (result != null && result.size() > 0) {
                for (int i = 0; i <result.size(); i++) {
                    listCheck.add(false);
                }
            }
        }
    }
}
