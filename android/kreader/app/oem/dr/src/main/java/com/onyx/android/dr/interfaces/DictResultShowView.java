package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.data.database.DictSettingEntity;

import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public interface DictResultShowView {
    void setDictTypeData(List<DictTypeBean> dictData);
    void setDictSettingData(List<DictSettingEntity> dictData);
}
