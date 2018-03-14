package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.rx.RxRequestChain;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxAppUpdateHistoryLoadRequest;
import com.onyx.jdread.setting.request.RxFirmwareHistoryLoadRequest;
import com.onyx.jdread.setting.utils.UpdateUtil;

/**
 * Created by suicheng on 2018/3/13.
 */
public class SystemUpdateHistoryAction extends BaseAction<SettingBundle> {

    private StringBuilder stringBuilder = new StringBuilder();

    public String getContent() {
        return stringBuilder.toString();
    }

    @Override
    public void execute(SettingBundle bundle, final RxCallback callback) {
        RxRequestChain chain = new RxRequestChain();
        final RxAppUpdateHistoryLoadRequest appRequest = new RxAppUpdateHistoryLoadRequest(UpdateUtil.getQueryAppUpdate(), true);
        final RxFirmwareHistoryLoadRequest firmwareRequest = new RxFirmwareHistoryLoadRequest(Firmware.currentFirmware(), true);
        chain.add(appRequest);
        chain.add(firmwareRequest);
        chain.execute(new RxCallback<RxRequest>() {

            @Override
            public void onSubscribe() {
                invokeSubscribe(callback);
            }

            @Override
            public void onNext(RxRequest rxRequest) {
                if (rxRequest == appRequest) {
                    appendContent(appRequest.getContent());
                }
                if (rxRequest == firmwareRequest) {
                    if (stringBuilder.length() > 0 && StringUtils.isNotBlank(firmwareRequest.getContent())) {
                        stringBuilder.append(Constants.NEW_LINE).append(Constants.NEW_LINE);
                    }
                    appendContent(firmwareRequest.getContent());
                }
                invokeNext(callback, SystemUpdateHistoryAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                invokeError(callback, throwable);
            }

            @Override
            public void onComplete() {
                invokeComplete(callback);
            }
        });
    }

    private void appendContent(String content) {
        if (StringUtils.isNullOrEmpty(content)) {
            return;
        }
        stringBuilder.append(content);
    }
}
