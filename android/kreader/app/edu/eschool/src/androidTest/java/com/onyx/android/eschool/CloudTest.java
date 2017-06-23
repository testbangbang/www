package com.onyx.android.eschool;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;

/**
 * Created by zhuzeng on 23/06/2017.
 */

public class CloudTest extends ApplicationTestCase<Application> {
    public CloudTest() {
        super(Application.class);
    }

}

