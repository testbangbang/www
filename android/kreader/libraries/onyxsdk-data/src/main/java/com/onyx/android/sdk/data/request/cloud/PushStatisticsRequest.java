package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.util.List;

import retrofit2.Response;

/**
 * Created by ming on 2017/2/7.
 */

public class PushStatisticsRequest extends BaseCloudRequest {

    private final static int MAX_PUSH_COUNT = 1000;
    private Context context;
    private List<OnyxStatisticsModel> saveStatistic;

    public PushStatisticsRequest(Context context, List<OnyxStatisticsModel> saveStatistic) {
        this.context = context;
        this.saveStatistic = saveStatistic;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (saveStatistic != null) {
            StatisticsUtils.saveStatisticsList(context, saveStatistic);
        }
        if (!DeviceUtils.isWifiConnected(context)) {
            return;
        }
        List<OnyxStatisticsModel> modelList = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsList(context, MAX_PUSH_COUNT, BaseStatisticsModel.DATA_STATUS_NOT_PUSH);
        if (modelList == null || modelList.size() <= 0) {
            return;
        }

        Response<JsonRespone> response = null;
        try {
            response = executeCall(ServiceFactory.getStatisticsService(parent.getCloudConf().getStatistics()).pushStatistics(modelList));
        } catch (Exception e) {

        }
        if (response != null && response.isSuccessful()) {
            for (OnyxStatisticsModel model : modelList) {
                model.setStatus(BaseStatisticsModel.DATA_STATUS_PUSHED);
            }
            StatisticsUtils.saveStatisticsList(context, modelList);
        }
    }

}
