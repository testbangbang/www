package com.onyx.edu.reader.ui.actions;

import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/9.
 */

public class AccountLoadFromLocalAction extends BaseAction {

    private StringBuffer token = new StringBuffer();

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), accountLoadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                NeoAccountBase account = accountLoadRequest.getAccount();
                if (account == null) {
                    Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.account_no_log_in), Toast.LENGTH_SHORT).show();
                } else {
                    token.append(account.token);
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public StringBuffer getToken() {
        return token;
    }

    public static AccountLoadFromLocalAction create() {
        return new AccountLoadFromLocalAction();
    }
}
