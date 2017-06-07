package com.onyx.android.eschool.action;

import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.events.AccountAvailableEvent;
import com.onyx.android.eschool.events.AccountTokenErrorEvent;
import com.onyx.android.eschool.events.HardwareErrorEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
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

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/5/18.
 */
public class AuthTokenAction extends BaseAction<LibraryDataHolder> {

    @Override
    public void execute(LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        requestAuthAccount(dataHolder, baseCallback);
    }

    private void requestAuthAccount(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final BaseAuthAccount account = AccountLoadFromCloudRequest.createAuthAccountFromHardware(dataHolder.getContext());
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
                            BaseCallback.invoke(baseCallback, request, null);
                            return;
                        }
                    }

                    if (ContentException.isCloudException(e)) {
                        processCloudException((ContentException.CloudException) e);
                    }
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
                sendAccountAvailableEvent(saveAccountRequest.getNeoAccountBase());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        requestChain.execute(dataHolder.getContext(), dataHolder.getCloudManager());
    }

    private void processCloudException(ContentException.CloudException exception) {
        sendAccountTokenErrorEvent();
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

}
