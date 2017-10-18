package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.DictSettingEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class DictSettingInsert extends BaseDataRequest {
    private DictSettingEntity dictSettingEntity;
    private DictSettingEntity dictSettingInfo;
    private boolean weatherInsert = true;

    public DictSettingInsert(DictSettingEntity dictSettingEntity) {
        this.dictSettingInfo = dictSettingEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        insertData();
    }

    private void insertData() {
        dictSettingInfo.insert();
    }

    public boolean whetherInsert() {
        List<DictSettingEntity> dataList = queryDataList();
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                dictSettingEntity = dataList.get(i);
                if (dictSettingInfo.tabName.equals(dictSettingEntity.tabName)) {
                    weatherInsert = false;
                    return true;
                }
            }
            if (weatherInsert) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public List<DictSettingEntity> queryDataList() {
        List<DictSettingEntity> dataList = new Select().from(DictSettingEntity.class).queryList();
        return dataList;
    }
}
