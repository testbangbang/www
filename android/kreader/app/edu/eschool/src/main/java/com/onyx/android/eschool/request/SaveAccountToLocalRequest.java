package com.onyx.android.eschool.request;

import com.alibaba.fastjson.JSON;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/5/27.
 */

public class SaveAccountToLocalRequest extends BaseCloudRequest {

    private NeoAccountBase neoAccountBase;

    public SaveAccountToLocalRequest(NeoAccountBase neoAccountBase) {
        this.neoAccountBase = neoAccountBase;
    }

    public void setNeoAccountBase(NeoAccountBase neoAccountBase) {
        this.neoAccountBase = neoAccountBase;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (neoAccountBase == null || StringUtils.isNullOrEmpty(neoAccountBase.info)) {
            return;
        }
        StudentAccount parseAccount = JSON.parseObject(neoAccountBase.info, StudentAccount.class);
        if (parseAccount != null) {
            parseAccount.token = parent.getToken();
            StudentAccount.saveAccount(getContext(), parseAccount);
        }
    }
}
