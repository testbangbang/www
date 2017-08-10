package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.SpeechTimeBean;
import com.onyx.android.dr.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/9.
 */
public class RecordTimeConfig {
    public List<SpeechTimeBean> recordTimeData = new ArrayList<>();
    String[] names = DRApplication.getInstance().getResources().getStringArray(R.array.speech_time_name);
    int[] numbers = DRApplication.getInstance().getResources().getIntArray(R.array.speech_time_number);

    public List<SpeechTimeBean> loadRecordTimeData(Context context) {
        for (int i = 0; i < Constants.RECORD_TIME; i++) {
            SpeechTimeBean bean = new SpeechTimeBean();
            bean.setName(names[i]);
            bean.setNumber(numbers[i]);
            recordTimeData.add(bean);
        }
        return recordTimeData;
    }
}
