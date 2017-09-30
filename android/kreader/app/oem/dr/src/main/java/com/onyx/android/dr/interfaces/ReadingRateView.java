package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.data.database.ReadingRateEntity;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface ReadingRateView {
    void setReadingRateData(List<ReadingRateEntity> dataList);
    void setInformalEssayByTime(List<InformalEssayEntity> dataList);
    void setInformalEssayByTitle(List<InformalEssayEntity> dataList);
}
