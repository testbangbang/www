package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/9.
 */
public class RecordTimeConfig {
    public List<String> recordTimeData = new ArrayList<>();

    public List<String> loadRecordTimeData(Context context) {
        for (int i = 1; i <= Constants.RECORD_TIME; i++) {
            recordTimeData.add(String.valueOf(i) + context.getString(R.string.record_time_data_minute));
        }
        return recordTimeData;
    }
}
