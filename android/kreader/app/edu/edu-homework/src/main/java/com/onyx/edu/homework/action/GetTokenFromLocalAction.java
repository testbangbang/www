package com.onyx.edu.homework.action;

import android.content.Context;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseAction;

/**
 * Created by ming on 2017/6/9.
 */

public class GetTokenFromLocalAction extends BaseAction {

    @Override
    public void execute(final Context context, final BaseCallback baseCallback) {
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        getCloudManager().submitRequest(context, accountLoadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                NeoAccountBase account = accountLoadRequest.getAccount();
                if (account == null) {
                    Toast.makeText(context, context.getString(R.string.account_no_log_in), Toast.LENGTH_SHORT).show();
                } else {
                    getCloudManager().setToken(account.token);
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
