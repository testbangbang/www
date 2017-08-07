package com.onyx.android.note;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.hanvon.core.Algorithm;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    public void testDistance() {
        float value = Algorithm.distance(0, 0, 1.0f, 0, 0.5f, 0.5f);
        assertTrue(Float.compare(value, 0.5f) == 0);

        value = Algorithm.distance(0, 0, 1.0f, 0, 1.5f, 0.5f);
        assertTrue(Float.compare(value, 0.5f) != 0);

        value = Algorithm.distance(0, 1.0f, 1.0f, 0, 0f, 0f);
        float target = (float)Math.sqrt(2) / 2.0f;
        assertTrue(Float.compare(value, target) == 0);
    }
}