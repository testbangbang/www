package com.onyx.android.sdk.statistics;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.utils.TestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class UMengTest extends ApplicationTestCase<Application> {


    public UMengTest() {
        super(Application.class);
    }

    public void testDocument() {
        UMeng uMeng = new UMeng();
        Map<String, String> args = new HashMap<>();
        args.put(uMeng.KEY_TAG, "5871bb2907fe65168c000f07");
        args.put(uMeng.CHANNEL_TAG, "normal");
        uMeng.init(getContext(), args);
        for (int i = 0; i < 10; ++i) {
            uMeng.onActivityResume(getContext());
//            uMeng.onDocumentOpenedEvent(getContext(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            uMeng.onActivityPause(getContext());
        }
        TestUtils.sleep(10 * 1000);
    }

}
