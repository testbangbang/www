package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.database.MemorandumEntity;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface MemorandumView {
    void setMemorandumData(List<MemorandumEntity> dataList);
}
