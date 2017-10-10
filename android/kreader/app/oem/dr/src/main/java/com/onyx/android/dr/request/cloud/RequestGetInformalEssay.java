package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.InformalEssayData;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.request.local.InformalEssayInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
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
public class RequestGetInformalEssay extends AutoNetWorkConnectionBaseCloudRequest {
    private final String param;
    private final InformalEssayData informalEssayData;
    private List<CreateInformalEssayBean> dataList = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public RequestGetInformalEssay(InformalEssayData informalEssayData, String param) {
        this.param = param;
        this.informalEssayData = informalEssayData;
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
                    insertInformalEssay(dataList.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertInformalEssay(CreateInformalEssayBean bean) {
        InformalEssayEntity entity = new InformalEssayEntity();
        entity.currentTime = bean.currentTime;
        entity.title = bean.title;
        entity.wordNumber = bean.wordNumber;
        entity.content = bean.content;
        final InformalEssayInsert req = new InformalEssayInsert(entity);
        informalEssayData.insertInformalEssay(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
