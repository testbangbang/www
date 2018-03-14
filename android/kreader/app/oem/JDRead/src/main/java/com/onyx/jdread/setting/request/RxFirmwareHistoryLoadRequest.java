package com.onyx.jdread.setting.request;

import com.google.gson.reflect.TypeToken;
import com.onyx.android.sdk.data.model.Firmware;
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

public class RxFirmwareHistoryLoadRequest extends RxBaseCloudRequest {
    private List<Firmware> historyList = new ArrayList<>();
    private Firmware requestFirmware;

    private boolean parseToContent;
    private String content;

    public RxFirmwareHistoryLoadRequest(Firmware firmware, boolean parseToContent) {
        this.requestFirmware = firmware;
        this.parseToContent = parseToContent;
    }

    public List<Firmware> getHistoryList() {
        return historyList;
    }

    public String getContent() {
        return content;
    }

    @Override
    public RxFirmwareHistoryLoadRequest call() throws Exception {
        String params = JSONObjectParseUtils.toJson(requestFirmware);
        historyList = done(CloudApiContext.getOnyxService(CloudApiContext.ONYX_EINK_API).getFirmwareHistoryList(params));
        if (parseToContent && !CollectionUtils.isNullOrEmpty(historyList)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Firmware firmware : historyList) {
                if (firmware == null || CollectionUtils.isNullOrEmpty(firmware.changeList)) {
                    continue;
                }
                appendText(stringBuilder, getTitle(firmware), firmware.getChangeLog());
            }
            content = removeLastNewLine(stringBuilder).toString();
        }
        return this;
    }

    private List<Firmware> done(Call<List<Firmware>> call) {
        EnhancedCall<List<Firmware>> enhancedCall = new EnhancedCall<>(call);
        Type type = new TypeToken<ArrayList<Firmware>>() {}.getType();
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

    private String getTitle(Firmware firmware) {
        String title = String.format(ResManager.getString(R.string.device_setting_version_number),
                String.valueOf(firmware.buildNumber)) + "  ";
        if (firmware.getUpdatedAt() != null) {
            title += ResManager.getString(R.string.update_time) + " " + DateTimeUtil.DATE_FORMAT_YYYYMMDD_2.format(firmware.getUpdatedAt());
        }
        return title;
    }
}
