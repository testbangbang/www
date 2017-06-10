package com.onyx.edu.reader.ui.actions;

import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.request.cloud.v2.AccountLoadFromLocalRequest;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/9.
 */

public class AccountLoadFromLocalAction extends BaseAction {

    private EduAccount account;
    private StringBuffer token = new StringBuffer();

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final AccountLoadFromLocalRequest localAccountRequest = new AccountLoadFromLocalRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), localAccountRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                account = (EduAccount) localAccountRequest.getAccount();
                if (account == null) {
                    Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.account_no_log_in), Toast.LENGTH_SHORT).show();
                }else {
                    token.append(account.token);
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public EduAccount getAccount() {
        return account;
    }

    public StringBuffer getToken() {
        return token;
    }
}
