package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.database.InformalEssayEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface InformalEssayView {
    void setInformalEssayData(List<InformalEssayEntity> dataList, ArrayList<Boolean> listCheck);
    void setInformalEssayByTime(List<InformalEssayEntity> dataList);
    void setInformalEssayByTitle(List<InformalEssayEntity> dataList);
    void createInformalEssay(boolean tag);
}
