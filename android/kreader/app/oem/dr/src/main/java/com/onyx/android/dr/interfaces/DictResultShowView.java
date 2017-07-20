package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.bean.DictFunctionBean;
import com.onyx.android.dr.bean.DictTypeBean;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface DictResultShowView {
    void setDictResultData(List<DictFunctionBean> functionData);
    void setDictTypeData(List<DictTypeBean> dictData);
}
