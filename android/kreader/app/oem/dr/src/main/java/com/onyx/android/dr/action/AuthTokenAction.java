package com.onyx.android.dr.action;

import android.content.Context;
import android.util.Log;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.manager.LeanCloudManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.data.request.cloud.v2.GenerateAccountInfoRequest;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.utils.NetworkUtil;

/**
 * Created by suicheng on 2017/5/18.
 */
public class AuthTokenAction extends BaseAction<LibraryDataHolder> {
    private static final String TAG = "AuthTokenAction";
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

        final CloudRequestChain requestChain = new CloudRequestChain();
        requestChain.setAbortException(false);
        addIndexLookupRequest(dataHolder, requestChain);
        addLoginRequest(dataHolder, requestChain, baseCallback);
        requestChain.execute(dataHolder.getContext(), dataHolder.getCloudManager());
    }

    private void addIndexLookupRequest(final LibraryDataHolder dataHolder, final CloudRequestChain requestChain) {
        if (!DeviceConfig.sharedInstance(dataHolder.getContext()).isUseCloudIndexServer()) {
            return;
        }
        final CloudIndexServiceRequest indexServiceRequest = new CloudIndexServiceRequest(DeviceConfig.sharedInstance(DRApplication.getInstance()).getCloudMainIndexServerApi(),
                createIndexService(dataHolder.getContext()));
        indexServiceRequest.setLocalLoadRetryCount(localLoadRetryCount);
        requestChain.addRequest(indexServiceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !IndexService.hasValidServer(indexServiceRequest.getResultIndexService())) {
                    Log.w(TAG, "indexService error,ready to use backup service");
                    useFallbackServerCloudConf(dataHolder.getContext(), dataHolder.getCloudManager());
                }
            }
        });
    }

    private void addLoginRequest(final LibraryDataHolder dataHolder, final CloudRequestChain requestChain, final BaseCallback baseCallback) {
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        accountLoadRequest.setLocalLoadRetryCount(localLoadRetryCount);
        requestChain.addRequest(accountLoadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    processCloudException(dataHolder.getContext(), e);
                } else {
                    NeoAccountBase eduAccount = accountLoadRequest.getAccount();
                    if (NeoAccountBase.isValid(eduAccount)) {
                        sendAccountAvailableEvent(eduAccount);
                    } else {
                        sendAccountTokenErrorEvent(dataHolder.getContext());
                        e = ContentException.TokenException();
                    }
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void processCloudException(Context context, Throwable e) {
        sendAccountTokenErrorEvent(context);
    }

    private void sendAccountAvailableEvent(final NeoAccountBase account) {
        final GenerateAccountInfoRequest generateAccountInfoRequest = new GenerateAccountInfoRequest(account);
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), generateAccountInfoRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
//                EventBus.getDefault().post(new AccountAvailableEvent(account));
            }
        });
    }

    private void sendAccountTokenErrorEvent(Context context) {
        NeoAccountBase errorAccount = new NeoAccountBase();
        errorAccount.name = context.getString(R.string.account_un_login);
        final GenerateAccountInfoRequest generateAccountInfoRequest = new GenerateAccountInfoRequest(errorAccount);
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), generateAccountInfoRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
//                EventBus.getDefault().post(new AccountTokenErrorEvent());
            }
        });
    }

    private void sendHardwareErrorEvent() {
//        EventBus.getDefault().post(new HardwareErrorEvent());
    }

    public void setLocalLoadRetryCount(int retryCount) {
        this.localLoadRetryCount = retryCount;
    }

    private IndexService createIndexService(Context context) {
        IndexService authService = new IndexService();
        authService.mac = NetworkUtil.getMacAddress(context);
        authService.installationId = LeanCloudManager.getInstallationId();
        return authService;
    }

    public static void useFallbackServerCloudConf(Context context, CloudManager cloudManager) {
        cloudManager.setAllCloudConf(CloudConf.create(
                DeviceConfig.sharedInstance(context).getCloudContentHost(),
                DeviceConfig.sharedInstance(context).getCloudContentApi(),
                Constant.DEFAULT_CLOUD_STORAGE));
        cloudManager.setCloudDataProvider(cloudManager.getCloudConf());
    }
}
