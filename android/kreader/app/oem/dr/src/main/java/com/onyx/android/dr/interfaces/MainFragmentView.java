package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by hehai on 17-8-4.
 */

public interface MainFragmentView {
    void setDatas(QueryResult<Metadata> queryResult);

    void setNowReading(QueryResult<Metadata> list);
}
