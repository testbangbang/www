package com.onyx.android.eschool.action;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.onyx.android.eschool.events.AccountAvailableEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.ContentAccount;
import com.onyx.android.sdk.data.model.v2.ContentAuthAccount;
import com.onyx.android.sdk.data.request.cloud.v2.ContentAccountGetRequest;
import com.onyx.android.sdk.data.request.cloud.v2.ContentAccountTokenRequest;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
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
        final CloudManager cloudManager = dataHolder.getCloudManager();
        if (StudentAccount.isAccountValid(dataHolder.getContext())) {
            cloudManager.setToken(StudentAccount.loadAccount(dataHolder.getContext()).token);
            sendAccountAvailableEvent();
            BaseCallback.invoke(baseCallback, null, null);
            return;
        }
        final ContentAuthAccount account = createContentAccount(dataHolder.getContext());
        if (account == null) {
            ToastUtils.showToast(dataHolder.getContext(), "当前wifi可能没有连接，获取不了mac地址");
            return;
        }
        CloudRequestChain requestChain = new CloudRequestChain();
        final ContentAccountTokenRequest tokenRequest = new ContentAccountTokenRequest(account);
        final ContentAccountGetRequest applyRequest = new ContentAccountGetRequest();
        requestChain.addRequest(tokenRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(request.getContext(), "token获取失败");
                }
            }
        });
        requestChain.addRequest(applyRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(request.getContext(), "个人信息获取失败");
                    return;
                }
                ContentAccount accountInfo = applyRequest.getAccount();
                saveStudentAccount(request.getContext(), accountInfo, cloudManager.getToken());
                sendAccountAvailableEvent();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        requestChain.execute(dataHolder.getContext(), cloudManager);
    }

    private void saveStudentAccount(Context context, ContentAccount contentAccount, String token) {
        if (contentAccount == null || StringUtils.isNullOrEmpty(contentAccount.info)) {
            return;
        }
        StudentAccount parseAccount = JSON.parseObject(contentAccount.info, StudentAccount.class);
        if (parseAccount != null) {
            parseAccount.token = token;
            parseAccount.accountInfo = contentAccount;
            parseAccount.saveAccount(context);
        }
    }

    private void sendAccountAvailableEvent() {
        EventBus.getDefault().post(new AccountAvailableEvent());
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
