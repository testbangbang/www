package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.bean.ReadingRateBean;
import com.onyx.android.dr.data.database.InformalEssayEntity;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface ReadingRateView {
    void setReadingRateData(List<ReadingRateBean> dataList);
    void setInformalEssayByTime(List<InformalEssayEntity> dataList);
    void setInformalEssayByTitle(List<InformalEssayEntity> dataList);
}
