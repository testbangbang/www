package com.onyx.android.sdk.statistics;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import java.util.UUID;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class UMengTest extends ApplicationTestCase<Application> {


    public UMengTest() {
        super(Application.class);
    }

    public void testDocument() {
        UMengWrapper.init(true, getContext(), "5871bb2907fe65168c000f07");
        for (int i = 0; i < 10; ++i) {
            UMengWrapper.activityResume(getContext());
            UMengWrapper.collectViewDocumentEvent(getContext(), UUID.randomUUID().toString());
            UMengWrapper.activityResume(getContext());
        }
    }

}
