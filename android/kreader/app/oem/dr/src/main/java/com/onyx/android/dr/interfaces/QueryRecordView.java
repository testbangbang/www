package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.database.QueryRecordEntity;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface QueryRecordView {
    void setQueryRecordData(List<QueryRecordEntity> list);
}
