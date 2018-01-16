package com.onyx.jdread.shop.common;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by li on 2018/1/15.
 */

public class RequestKeyComparator implements Comparator<Map.Entry<String, String>> {

    @Override
    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
        return o1.getKey().compareTo(o2.getKey());
    }
}
