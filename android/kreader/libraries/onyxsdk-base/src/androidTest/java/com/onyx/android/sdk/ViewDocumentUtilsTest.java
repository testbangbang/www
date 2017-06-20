package com.onyx.android.sdk;

import android.app.Application;
import android.content.Intent;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.utils.ViewDocumentUtils;

import java.io.File;

/**
 * Created by joy on 3/7/17.
 */

public class ViewDocumentUtilsTest extends ApplicationTestCase<Application> {

    public ViewDocumentUtilsTest() {
        super(Application.class);
    }

    public void testSlideShowMode() {
        File file = new File("/sdcard/Books/a.pdf");
        Intent intent = ViewDocumentUtils.autoSlideShowIntent(file, 1000, 10);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
