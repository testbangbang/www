package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.ReadingRateData;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.request.local.ReadingRateInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CreateReadingRateBean;
import com.onyx.android.sdk.data.model.v2.GetReadingRateBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/10/10.
 */
public class RequestGetReadingRate extends AutoNetWorkConnectionBaseCloudRequest {
    private final String param;
    private final String id;
    private final ReadingRateData readingRateData;
    private List<CreateReadingRateBean> dataList = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public RequestGetReadingRate(ReadingRateData readingRateData, String param, String id) {
        this.param = param;
        this.id = id;
        this.readingRateData = readingRateData;
    }

    public List<CreateReadingRateBean> getGroup() {
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
            Response<GetReadingRateBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).getReadingRate(id, param));
            if (response != null) {
                GetReadingRateBean body = response.body();
                dataList = body.list;
            }
            if (dataList != null && dataList.size() > 0) {
                listCheck.clear();
                for (int i = 0; i < dataList.size(); i++) {
                    listCheck.add(false);
                    insertData(dataList.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertData(CreateReadingRateBean bean) {
        ReadingRateEntity entity = new ReadingRateEntity();
        entity.cloudId = bean._id;
        entity.recordDate = bean.recordDate;
        entity.name = bean.name;
        entity.book = bean.book;
        entity.readTimeLong = bean.readTimeLong;
        entity.wordsCount = bean.wordsCount;
        entity.language = bean.language;
        entity.speed = bean.speed;
        entity.summaryCount = bean.summaryCount;
        entity.impressionCount = bean.impressionCount;
        entity.impressionWordsCount = bean.impressionWordsCount;
        final ReadingRateInsert req = new ReadingRateInsert(entity);
        readingRateData.insertReadingRate(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
