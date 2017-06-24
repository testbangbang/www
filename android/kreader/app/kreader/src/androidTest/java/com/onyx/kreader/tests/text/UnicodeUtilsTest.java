package com.onyx.kreader.tests.text;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.tests.ReaderTestActivity;
import com.onyx.android.sdk.utils.UnicodeUtils;

/**
 * Created by zengzhu on 3/13/16.
 */
public class UnicodeUtilsTest  extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private String TAG = UnicodeUtilsTest.class.getSimpleName();

    public UnicodeUtilsTest() {
        super(ReaderTestActivity.class);
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
