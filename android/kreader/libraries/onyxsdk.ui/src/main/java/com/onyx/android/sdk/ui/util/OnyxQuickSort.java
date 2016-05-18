package com.onyx.android.sdk.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.onyx.android.sdk.data.util.NumericUtil;
import com.onyx.android.sdk.ui.data.DirectoryItem;

public class OnyxQuickSort<T extends DirectoryItem>
{
    public void positiveSequence(ArrayList<T> items)
    {
        Collections.sort(items, new Comparator<DirectoryItem>() {
            public int compare(DirectoryItem a, DirectoryItem b)
            {
                double pa = Double.valueOf(a.getPage());
                double pb = Double.valueOf(b.getPage());
                if (NumericUtil.equalsAlmost(pa, pb)) {
                    return 0;
                }
                else if (pa < pb) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });
    }
}
