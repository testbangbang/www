package com.onyx.jdread.reader.utils;

import com.onyx.jdread.reader.highlight.SelectionInfo;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/3/4.
 */

public class MapKeyComparator implements Comparator<Map.Entry<String, SelectionInfo>> {

    @Override
    public int compare(Map.Entry<String, SelectionInfo> o1, Map.Entry<String, SelectionInfo> o2) {
        return o1.getKey().compareTo(o2.getKey());
    }
}
