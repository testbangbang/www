package com.onyx.kreader.ui.dialog;


import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.tests.ReaderTestActivity;

/**
 * Created by joy on 7/14/16.
 */
public class SubScreenModeTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public SubScreenModeTest() {
        super(ReaderTestActivity.class);
    }

    public void testSubScreenModeMatrix() {
        assertEquals(DialogNavigationSettings.SubScreenModeMatrix[0][0], DialogNavigationSettings.SubScreenMode.SUB_SCREEN_1_1);
        assertEquals(DialogNavigationSettings.SubScreenModeMatrix[0][2], DialogNavigationSettings.SubScreenMode.SUB_SCREEN_1_3);
        assertEquals(DialogNavigationSettings.SubScreenModeMatrix[1][1], DialogNavigationSettings.SubScreenMode.SUB_SCREEN_2_2);
        assertEquals(DialogNavigationSettings.SubScreenModeMatrix[2][0], DialogNavigationSettings.SubScreenMode.SUB_SCREEN_3_1);
        assertEquals(DialogNavigationSettings.SubScreenModeMatrix[2][2], DialogNavigationSettings.SubScreenMode.SUB_SCREEN_3_3);
    }
}