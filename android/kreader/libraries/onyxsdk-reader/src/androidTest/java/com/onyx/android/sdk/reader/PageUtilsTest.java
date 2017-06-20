package com.onyx.android.sdk.reader;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.reader.host.math.PageUtils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PageUtilsTest extends ApplicationTestCase<Application> {
    public PageUtilsTest() {
        super(Application.class);
    }

    public void testPageRepeat() {
        int pageHeight = 2000;
        int subHeight = 500;
        int repeat = 50;
        int count = PageUtils.countSubPagesRegardingPageRepeat(pageHeight, subHeight, repeat);
        Log.d(getClass().getSimpleName(), "page count: " + count);
        for (int i = 0; i < count; i++) {
            int top = PageUtils.getSubPageTopRegardingPageRepeat(subHeight, repeat, i);
            int bottom = top + subHeight;
            Log.d(getClass().getSimpleName(), "sub page: " + i + ", [" + top + ", " + bottom + "]");
        }
    }
}