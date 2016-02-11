package com.onyx.kreader.utils;

import com.onyx.kreader.host.math.PageInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by zhuzeng on 2/11/16.
 */
public class TestUtils {

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    static public boolean compareList(final List<PageInfo> a, final List<PageInfo> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for(PageInfo pageInfo : a) {
            if (!b.contains(pageInfo)) {
                return false;
            }
        }
        for(PageInfo pageInfo : b) {
            if (!a.contains(pageInfo)) {
                return false;
            }
        }
        return true;
    }

    static public boolean compareFloatWhole(float a, float b) {
        return ((int)a == (int)b);
    }

}
