package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.database.ReadingRateEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface ReadingRateView {
    void setReadingRateData(List<ReadingRateEntity> dataList);
    void setDataByTimeAndType(List<ReadingRateEntity> dataList, ArrayList<Boolean> listCheck);
}
