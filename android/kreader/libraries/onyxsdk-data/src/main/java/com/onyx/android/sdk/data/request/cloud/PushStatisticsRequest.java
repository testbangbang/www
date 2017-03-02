package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.StatisticsCloudManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by ming on 2017/2/7.
 */

public class PushStatisticsRequest extends BaseStatisticsRequest {

    private final static int MAX_PUSH_COUNT = 1000;
    private Context context;
    private List<OnyxStatisticsModel> saveStatistic;
    private String url;

    public PushStatisticsRequest(Context context, List<OnyxStatisticsModel> statistic, final String url) {
        this.context = context;
        this.saveStatistic = statistic;
        this.url = url;
    }

    @Override
    public void execute(StatisticsCloudManager parent) throws Exception {
        if (saveStatistic != null) {
            StatisticsUtils.saveStatisticsList(context, saveStatistic);
        }
        if (StringUtils.isNullOrEmpty(url)) {
            return;
        }
        if (!DeviceUtils.isWifiConnected(context)) {
            return;
        }
        String mac = DeviceUtils.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(mac)) {
            return;
        }
        List<OnyxStatisticsModel> statisticsModels = (List<OnyxStatisticsModel>) StatisticsUtils.loadStatisticsListByStatus(context, MAX_PUSH_COUNT, BaseStatisticsModel.DATA_STATUS_NOT_PUSH);
        if (statisticsModels == null || statisticsModels.size() <= 0) {
            return;
        }
        updateStatisticsMac(statisticsModels, mac);

        Response<JsonRespone> response = null;
        try {
            response = executeCall(ServiceFactory.getStatisticsService(url).pushStatistics(statisticsModels));
        } catch (Exception e) {

        }
        if (response != null && response.isSuccessful()) {
            for (OnyxStatisticsModel model : statisticsModels) {
                model.setStatus(BaseStatisticsModel.DATA_STATUS_PUSHED);
            }
            StatisticsUtils.saveStatisticsList(context, statisticsModels);
        }
    }

    private void updateStatisticsMac(final List<OnyxStatisticsModel> statistics, final String mac) {
        for (OnyxStatisticsModel onyxStatisticsModel : statistics) {
            onyxStatisticsModel.setMac(mac);
        }
    }

}
