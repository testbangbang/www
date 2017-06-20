package com.onyx.android.sdk.reader.tests.text;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.utils.UnicodeUtils;

/**
 * Created by zengzhu on 3/13/16.
 */
public class UnicodeUtilsTest  extends ApplicationTestCase<Application> {

    private String TAG = UnicodeUtilsTest.class.getSimpleName();

    public UnicodeUtilsTest() {
        super(Application.class);
    }{

    }

    public void testChinesePunctuation() {
        assertTrue(UnicodeUtils.isPunctuation('?'));
        assertTrue(UnicodeUtils.isPunctuation(','));
        assertTrue(UnicodeUtils.isPunctuation(','));
        assertTrue(UnicodeUtils.isPunctuation(';'));
        assertTrue(UnicodeUtils.isPunctuation('’'));
    }

}
