package com.onyx.android.eschool.action;

import android.content.Context;

import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.events.AccountAvailableEvent;
import com.onyx.android.eschool.events.AccountTokenErrorEvent;
import com.onyx.android.eschool.events.HardwareErrorEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.AccountLoadFromCloudRequest;
import com.onyx.android.sdk.data.request.cloud.v2.AccountLoadFromLocalRequest;
import com.onyx.android.sdk.data.request.cloud.v2.AccountSaveToLocalRequest;
import com.onyx.android.sdk.data.request.cloud.v2.GenerateAccountInfoRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
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
        final BaseAuthAccount account = createAuthAccountFromHardware(dataHolder.getContext());
        if (account == null) {
            sendHardwareErrorEvent();
            return;
        }
        CloudRequestChain requestChain = new CloudRequestChain();
        final AccountLoadFromLocalRequest localAccountRequest = new AccountLoadFromLocalRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        final AccountLoadFromCloudRequest accountGetRequest = new AccountLoadFromCloudRequest<>(account, EduAccount.class);
        final AccountSaveToLocalRequest saveAccountRequest = new AccountSaveToLocalRequest(EduAccountProvider.CONTENT_URI, null);
        requestChain.addRequest(localAccountRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (NeoAccountBase.isValid(localAccountRequest.getAccount())) {
                    sendAccountAvailableEvent(localAccountRequest.getAccount());
                }
            }
        });
        requestChain.addRequest(accountGetRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    if (ContentException.isNetworkException(e)) {
                        if (NeoAccountBase.isValid(localAccountRequest.getAccount())) {
                            updateRetrofit(dataHolder);
                            BaseCallback.invoke(baseCallback, request, null);
                            return;
                        }
                    }
                    sendAccountTokenErrorEvent();
                    return;
                }
                saveAccountRequest.setNeoAccountBase(accountGetRequest.getNeoAccount());
            }
        });
        requestChain.addRequest(saveAccountRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                updateRetrofit(dataHolder);
                sendAccountAvailableEvent(saveAccountRequest.getNeoAccountBase());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        requestChain.execute(dataHolder.getContext(), dataHolder.getCloudManager());
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

    private void sendAccountTokenErrorEvent() {
        EventBus.getDefault().post(new AccountTokenErrorEvent());
    }

    private void sendHardwareErrorEvent() {
        EventBus.getDefault().post(new HardwareErrorEvent());
    }

    public static BaseAuthAccount createAuthAccountFromHardware(Context context) {
        String macAddress = NetworkUtil.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(macAddress)) {
            return null;
        }
        return BaseAuthAccount.create(FileUtils.computeMD5(macAddress + NAME_SECRET),
                FileUtils.computeMD5(macAddress + PASSWORD_SECRET));
    }

    public static void updateRetrofit(LibraryDataHolder dataHolder) {
        ServiceFactory.addRetrofitTokenHeader(dataHolder.getCloudManager().getCloudConf().getApiBase(),
                Constant.HEADER_AUTHORIZATION,
                ContentService.CONTENT_AUTH_PREFIX + dataHolder.getCloudManager().getToken());
    }
}
