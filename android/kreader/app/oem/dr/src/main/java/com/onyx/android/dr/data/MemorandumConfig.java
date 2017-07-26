package com.onyx.android.dr.data;


import com.onyx.android.dr.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/26.
 */
public class MemorandumConfig {
    public List<String> hourList = new ArrayList<>();
    public List<String> minuteList = new ArrayList<>();

    public List<String> loadHourDatas() {
        for (int i = 1; i <= Constants.HOUR; i++) {
            hourList.add(String.valueOf(i));
        }
        return hourList;
    }

    public List<String> loadMinuteDatas() {
        for (int i = 1; i <= Constants.MINUTE; i++) {
            minuteList.add(String.valueOf(i));
        }
        return minuteList;
    }
}
