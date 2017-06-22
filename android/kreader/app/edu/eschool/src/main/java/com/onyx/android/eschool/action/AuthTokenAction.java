package com.onyx.android.eschool.action;

import android.content.Context;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.events.AccountAvailableEvent;
import com.onyx.android.eschool.events.AccountTokenErrorEvent;
import com.onyx.android.eschool.events.HardwareErrorEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.manager.LeanCloudManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;
import com.onyx.android.sdk.data.request.cloud.v2.GenerateAccountInfoRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.utils.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/5/18.
 */
public class AuthTokenAction extends BaseAction<LibraryDataHolder> {
    private int localLoadRetryCount = 1;

    @Override
    public void execute(LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        requestAuthAccount(dataHolder, baseCallback);
    }

    private void requestAuthAccount(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final BaseAuthAccount account = LoginByHardwareInfoRequest.createAuthAccountFromHardware(dataHolder.getContext());
        if (account == null) {
            sendHardwareErrorEvent();
            return;
        }
        setMainIndexServerCloudConf(dataHolder.getContext(), dataHolder.getCloudManager());
        final CloudRequestChain requestChain = new CloudRequestChain();
        final CloudIndexServiceRequest authServiceRequest = new CloudIndexServiceRequest(createAuthService(dataHolder.getContext()));
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        authServiceRequest.setLocalLoadRetryCount(localLoadRetryCount);
        accountLoadRequest.setLocalLoadRetryCount(localLoadRetryCount);
        requestChain.setAbortException(false);
        requestChain.addRequest(authServiceRequest, null);
        requestChain.addRequest(accountLoadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    processCloudException(dataHolder.getContext(), e);
                    return;
                }
                NeoAccountBase eduAccount = accountLoadRequest.getAccount();
                if (NeoAccountBase.isValid(eduAccount)) {
                    sendAccountAvailableEvent(eduAccount);
                    BaseCallback.invoke(baseCallback, request, null);
                } else {
                    sendAccountTokenErrorEvent(dataHolder.getContext());
                }
            }
        });
        requestChain.execute(dataHolder.getContext(), dataHolder.getCloudManager());
    }

    private void processCloudException(Context context, Throwable e) {
        sendAccountTokenErrorEvent(context);
    }

    private void sendAccountAvailableEvent(final NeoAccountBase account) {
        final GenerateAccountInfoRequest generateAccountInfoRequest = new GenerateAccountInfoRequest(account);
        SchoolApp.getSchoolCloudStore().submitRequest(SchoolApp.singleton(), generateAccountInfoRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EventBus.getDefault().post(new AccountAvailableEvent(account));
            }
        });
    }

    private void sendAccountTokenErrorEvent(Context context) {
        NeoAccountBase errorAccount = new NeoAccountBase();
        errorAccount.name = context.getString(R.string.account_un_login);
        final GenerateAccountInfoRequest generateAccountInfoRequest = new GenerateAccountInfoRequest(errorAccount);
        SchoolApp.getSchoolCloudStore().submitRequest(SchoolApp.singleton(), generateAccountInfoRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EventBus.getDefault().post(new AccountTokenErrorEvent());
            }
        });
    }

    private void sendHardwareErrorEvent() {
        EventBus.getDefault().post(new HardwareErrorEvent());
    }

    public void setLocalLoadRetryCount(int retryCount) {
        this.localLoadRetryCount = retryCount;
    }

    private IndexService createAuthService(Context context) {
        IndexService authService = new IndexService();
        authService.mac = NetworkUtil.getMacAddress(context);
        authService.installationId = LeanCloudManager.getInstallationId();
        return authService;
    }

    public static void setMainIndexServerCloudConf(Context context, CloudManager cloudManager) {
        cloudManager.setAllCloudConf(CloudConf.create(
                DeviceConfig.sharedInstance(context).getCloudMainIndexServerHost(),
                DeviceConfig.sharedInstance(context).getCloudMainIndexServerApi(),
                Constant.DEFAULT_CLOUD_STORAGE));
    }
}
