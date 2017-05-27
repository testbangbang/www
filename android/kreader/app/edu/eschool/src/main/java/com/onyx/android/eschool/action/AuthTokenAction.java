package com.onyx.android.eschool.action;

import android.content.Context;

import com.onyx.android.eschool.events.AccountAvailableEvent;
import com.onyx.android.eschool.events.AccountTokenErrorEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.eschool.request.LoadAccountFromLocalRequest;
import com.onyx.android.eschool.request.SaveAccountToLocalRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.model.v2.ContentAuthAccount;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.LoadAccountFromCloudRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/5/18.
 */
public class AuthTokenAction extends BaseAction<LibraryDataHolder> {
    public static final String NAME_SECRET = "eefbb54a-ffd1-4e86-9513-f83e15b807c9";
    public static final String PASSWORD_SECRET = "807bb28a-623e-408c-97c5-61177091737b";


    @Override
    public void execute(LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        requestAuthAccount(dataHolder, baseCallback);
    }

    private void requestAuthAccount(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final ContentAuthAccount account = createContentAccount(dataHolder.getContext());
        if (account == null) {
            ToastUtils.showToast(dataHolder.getContext(), "当前wifi可能没有连接，获取不了mac地址");
            return;
        }
        CloudRequestChain requestChain = new CloudRequestChain();
        final LoadAccountFromLocalRequest localAccountRequest = new LoadAccountFromLocalRequest();
        final LoadAccountFromCloudRequest accountGetRequest = new LoadAccountFromCloudRequest(account);
        final SaveAccountToLocalRequest saveAccountRequest = new SaveAccountToLocalRequest(null);
        requestChain.addRequest(localAccountRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (StudentAccount.isAccountValid(request.getContext(), localAccountRequest.getStudentAccount())) {
                    sendAccountAvailableEvent();
                }
            }
        });
        requestChain.addRequest(accountGetRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    if (ContentException.isNetworkException(e)) {
                        if (StudentAccount.isAccountValid(request.getContext(), localAccountRequest.getStudentAccount())) {
                            BaseCallback.invoke(baseCallback, request, null);
                            return;
                        }
                    }
                    sendAccountTokenErrorEvent();
                    return;
                }
                saveAccountRequest.setContentAccount(accountGetRequest.getContentAccount());
            }
        });
        requestChain.addRequest(saveAccountRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                sendAccountAvailableEvent();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        requestChain.execute(dataHolder.getContext(), dataHolder.getCloudManager());
    }

    private void sendAccountAvailableEvent() {
        EventBus.getDefault().post(new AccountAvailableEvent());
    }

    private void sendAccountTokenErrorEvent() {
        EventBus.getDefault().post(new AccountTokenErrorEvent());
    }

    public static ContentAuthAccount createContentAccount(Context context) {
        String macAddress = DeviceUtils.getDeviceMacAddress(context);
        if (StringUtils.isNullOrEmpty(macAddress)) {
            return null;
        }
        return ContentAuthAccount.create(FileUtils.computeMD5(macAddress + NAME_SECRET),
                FileUtils.computeMD5(macAddress + PASSWORD_SECRET));
    }
}
