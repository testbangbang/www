package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.data.ReadingRateData;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.v2.GetInformalEssayBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/10/10.
 */
public class RequestGetReadingRate extends AutoNetWorkConnectionBaseCloudRequest {
    private final String param;
    private final ReadingRateData readingRateData;
    private List<CreateInformalEssayBean> dataList = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public RequestGetReadingRate(ReadingRateData readingRateData, String param) {
        this.param = param;
        this.readingRateData = readingRateData;
    }

    public List<CreateInformalEssayBean> getGroup() {
        return dataList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<GetInformalEssayBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).getInformalEssay(param));
            if (response != null) {
                GetInformalEssayBean body = response.body();
                dataList = body.list;
            }
            if (dataList != null && dataList.size() > 0) {
                listCheck.clear();
                for (int i = 0; i < dataList.size(); i++) {
                    listCheck.add(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
