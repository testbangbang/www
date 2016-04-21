package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderScalePresets;
import junit.framework.Assert;

/**
 * Created by Joy on 2016/4/21.
 */
public class ReaderScalePresetsTest extends ActivityInstrumentationTestCase2<ReaderActivity> {
    static private String TAG = ReaderScalePresetsTest.class.getSimpleName();

    public ReaderScalePresetsTest() {
        super(ReaderActivity.class);
    }

    public void testScaleDown() {
        Log.d(TAG, "scale down of 5.0: " + ReaderScalePresets.scaleDown(5.0f));
        Assert.assertEquals(ReaderScalePresets.scaleDown(5.0f), 4.0f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(4.0f), 3.5f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(3.6f), 3.5f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(3.5f), 3.0f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(3.4f), 3.0f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(0.11f), 0.1f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(0.1f), 0.1f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(0.0f), 0.1f);
        Assert.assertEquals(ReaderScalePresets.scaleDown(-1.0f), 0.1f);
    }

    public void testScaleUp() {
        Log.d(TAG, "scale up of -1.0: " + ReaderScalePresets.scaleUp(-1.0f));
        Assert.assertEquals(ReaderScalePresets.scaleUp(-1.0f), 0.1f);
        Assert.assertEquals(ReaderScalePresets.scaleUp(0.0f), 0.1f);
        Assert.assertEquals(ReaderScalePresets.scaleUp(0.1f), 0.25f);
        Assert.assertEquals(ReaderScalePresets.scaleUp(0.24f), 0.25f);
        Assert.assertEquals(ReaderScalePresets.scaleUp(0.25f), 0.5f);
        Assert.assertEquals(ReaderScalePresets.scaleUp(0.26f), 0.5f);
        Assert.assertEquals(ReaderScalePresets.scaleUp(4.0f), 4.0f);
        Assert.assertEquals(ReaderScalePresets.scaleUp(5.0f), 4.0f);
    }
}
