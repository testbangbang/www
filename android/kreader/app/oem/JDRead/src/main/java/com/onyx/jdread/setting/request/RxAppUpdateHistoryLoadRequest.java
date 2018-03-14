package com.onyx.jdread.setting.request;

import com.google.gson.reflect.TypeToken;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by suicheng on 2018/3/13.
 */
public class RxAppUpdateHistoryLoadRequest extends RxBaseCloudRequest {
    private List<ApplicationUpdate> historyList = new ArrayList<>();
    private ApplicationUpdate requestAppUpdate;

    private boolean parseToContent;
    private String content;

    public RxAppUpdateHistoryLoadRequest(ApplicationUpdate appUpdate, boolean parseToContent) {
        this.requestAppUpdate = appUpdate;
        this.parseToContent = parseToContent;
    }

    public List<ApplicationUpdate> getHistoryList() {
        return historyList;
    }

    public String getContent() {
        return content;
    }

    @Override
    public RxAppUpdateHistoryLoadRequest call() throws Exception {
        String params = JSONObjectParseUtils.toJson(requestAppUpdate);
        historyList = done(CloudApiContext.getOnyxService(CloudApiContext.ONYX_EINK_API).getAppsHistoryList(params));
        if (parseToContent && !CollectionUtils.isNullOrEmpty(historyList)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ApplicationUpdate appUpdate : historyList) {
                if (appUpdate == null || CollectionUtils.isNullOrEmpty(appUpdate.getChangeLogList())) {
                    continue;
                }
                appendText(stringBuilder, getTitle(appUpdate), appUpdate.getChangeLog());
            }
            content = removeLastNewLine(stringBuilder).toString();
        }
        return this;
    }

    private List<ApplicationUpdate> done(Call<List<ApplicationUpdate>> call) {
        EnhancedCall<List<ApplicationUpdate>> enhancedCall = new EnhancedCall<>(call);
        Type type = new TypeToken<ArrayList<ApplicationUpdate>>() {}.getType();
        return enhancedCall.execute(call, type);
    }

    private void appendText(StringBuilder stringBuilder, String title, String content) {
        if (StringUtils.isNullOrEmpty(title) || StringUtils.isNullOrEmpty(content)) {
            return;
        }
        stringBuilder.append(title).append(Constants.NEW_LINE);
        stringBuilder.append(content).append(Constants.NEW_LINE).append(Constants.NEW_LINE);
    }

    private StringBuilder removeLastNewLine(StringBuilder stringBuilder) {
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1).deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder;
    }

    private String getTitle(ApplicationUpdate appUpdate) {
        String title = String.format(ResManager.getString(R.string.device_setting_version_number), appUpdate.versionName) + "  ";
        if (appUpdate.getUpdatedAt() != null) {
            title += ResManager.getString(R.string.update_time) + " " + DateTimeUtil.DATE_FORMAT_YYYYMMDD_2.format(appUpdate.getUpdatedAt());
        }
        return title;
    }
}
