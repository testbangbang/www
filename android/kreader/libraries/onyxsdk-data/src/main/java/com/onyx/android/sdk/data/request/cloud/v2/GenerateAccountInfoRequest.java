package com.onyx.android.sdk.data.request.cloud.v2;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.utils.BitmapUtils;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by zhuzeng on 03/06/2017.
 */

public class GenerateAccountInfoRequest extends BaseCloudRequest {
    private NeoAccountBase authAccount;

    public GenerateAccountInfoRequest(NeoAccountBase authAccount) {
        this.authAccount = authAccount;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        BitmapUtils.buildBitmapFromText(authAccount.getName() + " " + authAccount.getFirstGroup(),
                100, 25, true,
                true, true, true, 90,
                "data/local/assets/info.png");
    }

}