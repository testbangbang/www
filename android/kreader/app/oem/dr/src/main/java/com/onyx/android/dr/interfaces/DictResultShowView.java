package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.data.DictFunctionData;
import com.onyx.android.dr.data.DictTypeData;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface DictResultShowView {
    void setDictResultData(List<DictFunctionData> functionData);
    void setDictTypeData(List<DictTypeData> dictData);
}
