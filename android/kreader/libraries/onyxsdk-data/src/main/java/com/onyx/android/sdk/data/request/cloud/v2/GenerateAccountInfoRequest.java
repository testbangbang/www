package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.StringUtils;


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
        String targetString = authAccount.getName() + " " + authAccount.getFirstGroup();
        BitmapUtils.buildBitmapFromText(StringUtils.getBlankStr(targetString).trim(),
                100, 25, true,
                true, true, true, 90,
                "data/local/assets/info.png");
    }

}