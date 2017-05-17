package com.onyx.edu.reader.host.impl;

import android.test.ActivityInstrumentationTestCase2;

import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;
import com.onyx.edu.reader.tests.ReaderTestActivity;

/**
 * Created by joy on 7/21/16.
 */
public class ReaderTextSplitterImplTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderTextSplitterImplTest() {
        super(ReaderTestActivity.class);
    }

    public void testGetTextSentenceBreakPoint() {
        assertEquals(ReaderTextSplitterImpl.sharedInstance().getTextSentenceBreakPoint("。"), 0);
        assertEquals(ReaderTextSplitterImpl.sharedInstance().getTextSentenceBreakPoint("我。"), 1);
    }

}